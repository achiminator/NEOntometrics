from fileinput import filename
from django.db.models.fields.related import ManyToManyField
from django.test import TestCase
from rest.CalculationHelper import GitUrlParser
from rest.CalculationManager import CalculationManager
from graphene_django.utils.testing import GraphQLTestCase

from rest.metricOntologyHandler import OntologyHandler
import json, requests

from rest.models import Commit, OntologyFile, Repository
# Create your tests here.
# Unit Tests

class URLParserTest(TestCase):
    """Tests the class that converts URLs to the internal GitURLParser Object"""
    def testSingleOntology(self):
        """Checks if a single Ontology is recognized properly"""
        url = GitUrlParser()
        url.parse("https://purl.industrialontologies.org/ontology/core/Core")
        self.assertEqual("", url.repository) # If a single ontology is loaded, the repository-field of the url object shall be empty.
    def testInvalidOntology(self):
        url = GitUrlParser()
        url.parse("https://purl.industrialonasdftologies.org/ontology/core/Core")
        self.assertFalse(url.validResource)
    def testWholeRepository(self):
        """Checks if a Whole repository is loaded parset correctly."""
        url = GitUrlParser()
        url.parse("https://github.com/achiminator/TestOnto")
        print(url.repository)
        self.assertEqual("github.com/achiminator/TestOnto", url.repository) 
        self.assertEqual("", url.file)
        self.assertEqual(url.service, "github.com")
    
    def testFileOfRepository(self):
        """Checks if the combination of file and repository is recognized propertly"""
        url = GitUrlParser()
        url.parse("https://github.com/achiminator/TestOnto/blob/main/testOntology2.owl")
        self.assertEqual("github.com/achiminator/TestOnto", url.repository) 
        self.assertEqual("testOntology2.owl", url.file)
        self.assertEqual(url.service, "github.com") 



class OntologyAnalysisTests(TestCase):
    """tests whether the Ontologies are analyzed properly."""
    calcManager = CalculationManager()
    
    def testSingleOntology(self):
        """Tests if a single ontology is recognized and analyzed properly"""
        url = GitUrlParser()
        url.parse("https://purl.industrialontologies.org/ontology/core/Core")
        self.calcManager.ontologyFileWORepo(url, True)
        res = OntologyFile.objects.filter(fileName=url.file)
        self.assertEqual(1, len(res))
        resInner = Commit.objects.filter(metricSource = res[0])
        self.assertEqual(1, len(resInner))
        self.assertGreater(resInner[0].axioms, 0)

    def testRepository(self):
        """Tests if a repository is recognized and analyzed properly"""
        url = GitUrlParser()
        url.parse("https://github.com/achiminator/TestOnto")
        self.calcManager.getObjects(url.repository, False, False)
        res = Repository.objects.get(repository = url.repository)
        resInner = OntologyFile.objects.filter(repository = res)
        self.assertGreater(len(resInner), 0)
        
class GraphQLTests(GraphQLTestCase):
    """Tests a running GraphQL Instance"""
    GRAPHQL_URL = "http://localhost:8086/graphql"
    def testMutateQueue(self):
        res = self.query("""mutation update_queueInfo{
            update_queueInfo(
                reasoner: false
                update: true
                url: "https://github.com/kbarber/puppet-ontologies/"
            ) {
                error
                errorMessage
                queueInformation {
                urlInSystem
                taskFinished
                performsUpdate
                taskStarted
                queuePosition
                analyzedCommits
                totalCommits
                analyzedOntologies
                analysableOntologies
                url
                repository
                service
                fileName
                error
                errorMessage
                }
            }
        }
        """, op_name="update_queueInfo")
        self.assertResponseNoErrors(res)
        self.assertFalse(json.loads(res.content)["data"]["update_queueInfo"]["error"])

    def testGetQuery(self):
        res = self.query("""query getRepository{getRepository(repository: "https://github.com/kbarber/puppet-ontologies/") {
            edges {
            node {
                repository
                ontologyfile_set {
                edges {
                    node {
                    id
                    fileName
                    branch
                    commit {
                        edges {
                        node {
                            pk
                            Size
                            AuthorEmail
                            anonymousClasses
                        }
                        }
                    }
                    }
                }
                }
            }
            }
        }
        }
        """, op_name="getRepository")
        self.assertResponseNoErrors(res)
        res = json.loads(res.content)
        print(res)
        

class ConsistencyTests(TestCase):
    
    ontology = OntologyHandler()
    ontologyImplementationStatements = ontology.getImplementedMetrics()
    def testConsistencyOntologyDatabase(self):
        """Checks if the indidivuals in the ontology (they state that a metric is implemented)
        corresponds to the database model
        """
        ontology = OntologyHandler()
        dbFields =  Commit._meta.get_fields()
        dbFieldsName = []
        for field in dbFields:
            if(not(isinstance(field, ManyToManyField))):
                dbFieldsName.append(field.name)
        for ontologyMetric in self.ontologyImplementationStatements:
            self.assertIn(ontologyMetric, dbFieldsName)
    def testConsistencyOntologyOPI(self):
        url = "https://raw.githubusercontent.com/Data-Semantics-Laboratory/MaterialsPropertyOwl/master/matl-prop.owl"
        resp = requests.get("http://localhost:8085/api?url={0}".format(url))
        self.assertEqual(200, resp.status_code, "Connection to the OPI-Service does not work") # Check if the HTML response code is 200 (okay)
        opiContent = json.loads(resp.content)
        for item in self.ontologyImplementationStatements:
            self.assertIn(item, opiContent["GeneralOntologyMetrics"], "an Element that is in the ontology is not in the OPI-Reponse")
        for item in opiContent["GeneralOntologyMetrics"]:
            self.assertIn(item, self.ontologyImplementationStatements, "an element that is in OPI ist not in the ontology")
    def metricCalculationTest(self):
        metricDict = self.ontology.getMetricDict()
        
        
