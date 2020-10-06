import pygit2 as Git
import logging
from rest.models import Metrics, Source, ClassMetrics
from os import path
from shutil import rmtree
from datetime import datetime
from rest.opiHandler import OpiHandler

class GitHandler:
    def getObject(self, repositoryUrl, objectLocation, branch="master", classMetrics=False):
            logger = logging.getLogger(__name__)
            internalOntologyUrl = "ontologies/" + str(hash(objectLocation))
            #internalOntologyUrl = "ontologies/" + str(1265874487)
            if(path.exists(internalOntologyUrl) == False):
                Git.clone_repository(repositoryUrl, internalOntologyUrl, checkout_branch=branch)
                logger.debug("Repository cloned at "+ internalOntologyUrl)       
            repo = Git.Repository(internalOntologyUrl)
            index = repo.index
            index.read()
            fileId = index[objectLocation].id
            file = repo.get(fileId)
            opi = OpiHandler
            formerObj= None
            returnBuilder = []
           # returnBuilder["branch"] = branch
            counter = 0
            for commit in repo.walk(repo.head.target, Git.GIT_SORT_TIME | Git.GIT_SORT_REVERSE):
                obj = self.getFittingObject(objectLocation.split("/"), commit.tree)
                if obj != None:
                    if(formerObj != self.getFittingObject(objectLocation.split("/"), commit.tree)):
                        counter += 1
                        formerObj = self.getFittingObject(objectLocation.split("/"), commit.tree)
                        returnObject = {}
                        returnObject["CommitTime"] = datetime.fromtimestamp(commit.commit_time)
                        returnObject["CommitMessage"] = commit.message
                        returnObject["AuthorName"] = commit.author.name
                        returnObject["AuthorEmail"] = commit.author.email
                        returnObject["CommitterName"] = commit.committer.name
                        returnObject["CommiterEmail"] = commit.committer.email
                        returnObject.update(opi.opiOntologyRequest(obj.data, classMetrics=ClassMetrics))
                        returnBuilder.append(returnObject)
                        
            rmtree(internalOntologyUrl, ignore_errors=True)
            sourceModel = Source.objects.create(fileName=objectLocation, repository=repositoryUrl,branch=branch)
            for commit in returnBuilder:
                metricsModel = Metrics.objects.create(CommitTime = commit["CommitTime"], metricSource=sourceModel)                
                modelDict = self.commit2MetricsModel(commit)
                metricsModel.__dict__.update(modelDict)
                metricsModel.save()
                if(classMetrics):
                    for classMetricsValues in commit["OntologyMetrics"]["BaseMetrics"]["Classmetrics"]:
                        classMetricsModel = classMetricsValues.objects.create(metric = metricsModel)
                        classMetricsModel.__dict__.update(classMetricsValues)
                        classMetricsModel.save()
            return(returnBuilder)

    def commit2MetricsModel(self, commit: dict): 
    #    """Takes the XML-Output and prepares it for writing into the Database"""
        tmpDict = {}
        tmpDict["CommitTime"] = commit["CommitTime"]
        tmpDict["CommitMessage"] = commit["CommitMessage"]
        tmpDict["AuthorName"] = commit["AuthorName"]
        tmpDict["AuthorEmail"] = commit["AuthorEmail"]
        tmpDict["CommitterName"] = commit["CommitterName"]
        tmpDict["CommiterEmail"] = commit["CommiterEmail"]
        tmpDict.update(commit["OntologyMetrics"]["BaseMetrics"]["Basemetrics"])
        tmpDict.update(commit["OntologyMetrics"]["BaseMetrics"]["Classaxioms"])
        tmpDict.update(commit["OntologyMetrics"]["BaseMetrics"]["Objectpropertyaxioms"])
        tmpDict.update(commit["OntologyMetrics"]["BaseMetrics"]["Datapropertyaxioms"])
        tmpDict.update(commit["OntologyMetrics"]["BaseMetrics"]["Individualaxioms"])
        tmpDict.update(commit["OntologyMetrics"]["BaseMetrics"]["Annotationaxioms"])
        tmpDict.update(commit["OntologyMetrics"]["BaseMetrics"]["Schemametrics"])
        tmpDict.update(commit["OntologyMetrics"]["BaseMetrics"]["Knowledgebasemetrics"])
        tmpDict.update(commit["OntologyMetrics"]["BaseMetrics"]["Graphmetrics"])
        return tmpDict

    def getFittingObject(self, searchLocation: list, treeObject):
            breadCrumbCounter = 0
            if(len(searchLocation) > 1):
                for breadCrumb in searchLocation:
                # Check if its the last element of the BreadCrumb Object
                    if(treeObject.__contains__(breadCrumb)):
                        searchLocation.pop(0)
                        return(self.getFittingObject(searchLocation, treeObject.__getitem__(breadCrumb)))
                    else:
                        return None
            else:
                if(treeObject.__contains__(searchLocation[0])):
                    if(type(treeObject.__getitem__(searchLocation[0])) is Git.Tree):
                        self.getFittingObject(searchLocation[0], treeObject.__getitem__(searchLocation[0]))
                    else:
                        return(treeObject.__getitem__(searchLocation[0]))