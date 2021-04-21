from rest import views
from rest_framework.test import APIRequestFactory
from django.test import TestCase
from rest.git import GitHandler

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
        self.factory = APIRequestFactory()
        self.view = views.CalculateGitMetric.as_view()
    
    def test_sweetHuman(self):
        """analyses the sweet Human Ontology"""
        request = self.factory.get("/", {"url": "https://github.com/ESIPFed/sweet/blob/master/src/human.ttl", "class":"False" })
        resp = self.view(request)
        self.assertIn("taskedFinished", resp.data)
        resp = self.view(request)
        self.assertIn("branch", resp.data[0])
    
    def test_puppetOntologyWhole(self):
        """Downloads the Whole Puppet Ontology without ClassMetrics
        """
        request = self.factory.get("/", {"url": "https://github.com/kbarber/puppet-ontologies/", "class":"False" })
        resp = self.view(request)
        self.assertIn("taskedFinished", resp.data)
        resp = self.view(request)
        self.puppetSize = resp.data.size
        self.assertIn("branch", resp.data[0])
    
    def test_puppetOntologyWholeAgain(self):
        """Queries the  Puppet Ontology again (It should not be downloaded again)
        """
        request = self.factory.get("/", {"url": "https://github.com/kbarber/puppet-ontologies/", "class":"False" })
        resp = self.view(request)
        self.assertIn("branch", resp.data[0])
    
    def test_puppetOntologySingleWithoutClassMetrics(self):
        """Tests a single puppet Ontology WITHOUT classes
        """
        request = self.factory.get("/", {"url": "https://github.com/kbarber/puppet-ontologies/blob/master/puppet-disco/base-types.owl", "class":"False" })
        resp = self.view(request)
        self.assertIn("branch", resp.data)

    def test_puppetOntologySingleWithClassMetrics(self):
        """Tests a single puppet Ontology WITH classMetrics
        """
        request = self.factory.get("/", {"url": "https://github.com/kbarber/puppet-ontologies/blob/master/puppet-disco/base-types.owl", "class":"True" })
        resp = self.view(request)
        self.assertIn("taskedFinished", resp.data)
        resp = self.view(request)
        self.assertIn("ClassMetrics", resp.data)
    def deleteSinglePuppetOntology(self):
        """Delets one ontology of the puppet repository
        """
        request = self.factory.delete("/", {"url": "https://github.com/kbarber/puppet-ontologies/blob/master/puppet-disco/base-types.owl", "class":"True" })
        resp = self.view(request)
        self.assertEqual(200, resp.status_code)
    def test_puppetOntologyWholeAfterDelete(self):
        """Prior to the execution of this test, one Ontology has been deleted. The Number should still be the same like in the first
        """
        request = self.factory.get("/", {"url": "https://github.com/kbarber/puppet-ontologies/", "class":"False" })
        resp = self.view(request)
        resp = self.view(request)
        self.assertEqual(resp.data.size, self.puppetSize)
