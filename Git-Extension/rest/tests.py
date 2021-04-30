from ontoMetricsAPI.settings import DATABASES
from rest import views
from rest_framework.test import APIRequestFactory
from django.test import TestCase, utils
from rest.git import GitHandler
import json, time
# Create your tests here.
# Unit Tests
# class GitMetricsTest(TestCase):
#     git = GitHandler()
#     def test_SingleSweetOntology(self):
#         git = GitHandler()
#         success = git.getObject(repositoryUrl="github.com/ESIPFed/sweet", objectLocation="src/human.ttl", classMetrics="True")
#         self.assertTrue(dict)
#     def test_WholePuppetOntology(self):
#         git = GitHandler()
#         success = git.getObjects(repositoryUrl="https://github.com/kbarber/puppet-ontologies/", objectLocation="src/human.ttl", classMetrics="True")



class RESTTests(TestCase):
    puppetSize = 0
    

    def setUp(self):
        utils.teardown_test_environment()
        utils.setup_test_environment()
    def test_sweetHuman(self):
        """analyses the sweet Human Ontology"""
        respData = self.asyncQuery("https://github.com/ESIPFed/sweet/blob/master/src/human.ttl")
        self.assertIn("branch", respData[0])
    
    def test_puppetOntologyWhole(self):
        """Downloads the Whole Puppet Ontology without ClassMetrics
        """
        
        respData = self.asyncQuery("https://github.com/kbarber/puppet-ontologies/")
        self.assertIn("branch", respData[0])
    
    def test_puppetOntologyWholeAgain(self):
        """Queries the  Puppet Ontology again (It should not be downloaded again)
        """
        respData = self.asyncQuery("https://github.com/kbarber/puppet-ontologies/")
        self.assertIn("branch", respData[0])
    
    def test_puppetOntologySingleWithoutClassMetrics(self):
        """Tests a single puppet Ontology WITHOUT classes
        """
        respData = self.asyncQuery("https://github.com/kbarber/puppet-ontologies/blob/master/puppet-disco/base-types.owl")
        self.assertIn("branch", respData)

    def test_puppetOntologySingleWithClassMetrics(self):
        """Tests a single puppet Ontology WITH classMetrics
        """
        resp = self.asyncQuery("https://github.com/kbarber/puppet-ontologies/blob/master/puppet-disco/base-types.owl", True)
        self.assertIn("ClassMetrics", respData)
    def deleteSinglePuppetOntology(self):
        """Delets one ontology of the puppet repository
        """
        resp = self.client.delete("/git", {"url": "https://github.com/kbarber/puppet-ontologies/blob/master/puppet-disco/base-types.owl", "class":"True" })
        self.assertEqual(200, resp.status_code)
    def test_puppetOntologyWholeAfterDelete(self):
        """Prior to the execution of this test, one Ontology has been deleted. The Number should still be the same like in the first
        """
        respData = self.asyncQuery("https://github.com/kbarber/puppet-ontologies/")
        self.assertEqual(len(respData), 5)
    
    def asyncQuery(self, url: str, classMetrics=False) -> dict:
        """Handles the Asynchronous Nature of the Service

        Args:
            url (str): [description]
            classMetrics (bool, optional): [description]. Defaults to False.

        Returns:
            dict: [description]
        """
        resp = self.client.get("/git", {"url": url, "class": str(classMetrics)})
        while "taskFinished" in json.loads(resp.content):
            time.sleep(2)
            resp = self.client.get("/git", {"url": url, "class": str(classMetrics)})
            print(resp)
            print(resp.content)
        return json.loads(resp.content)
        