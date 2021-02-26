import pygit2 as Git
import logging
from rest.models import Metrics, Source, ClassMetrics
from os import path
from shutil import rmtree
from datetime import datetime
from rest.opiHandler import OpiHandler

class GitHandler:
    """Handles the Git-Based Ontology Analysis
    """
    def getObject(self, repositoryUrl: str, objectLocation: str, branch="master", classMetrics=False) -> dict:
        """Analyses one ontology-file for evolutional ontology metrics

        Args:
            repositoryUrl (string): URL to remote Repository
            objectLocation (string): relative path to targeted ontology file in Repository
            branch (str, optional): selected branch. Defaults to "master".
            classMetrics (bool, optional): Enables the calculation of Class Metrics (Computational Expensive). Defaults to False.

        Returns:
            dict: Evolutional Ontology Metrics
        """
        logger = logging.getLogger(__name__)
        #Creates a folder for the git-Repository based on the hash of the repository URL.
        #internalOntologyUrl = "ontologies/" + str(hash(repositoryUrl))#
        internalOntologyUrl = "ontologies/" + str(-8901500850878917509)
        if(path.exists(internalOntologyUrl) == False):
            Git.clone_repository(repositoryUrl, internalOntologyUrl, checkout_branch=branch)
            logger.debug("Repository cloned at "+ internalOntologyUrl)       
        repo = Git.Repository(internalOntologyUrl)
        metrics = self.getOntologyMetrics(objectLocation, classMetrics, internalOntologyUrl, repositoryUrl, branch, repo)
        # rmtree(internalOntologyUrl, ignore_errors=True)
        
        return(metrics)

    def getOntologyMetrics(self, objectLocation: str, classMetrics: bool, internalOntologyUrl: str, remoteLocation: str, branch: str, repo: Git.Repository) -> dict:
        """Calculates Evolutional Ontology-Metrics for one ontology file and stores them into a database

        Args:
            objectLocation (string): relative path of requested file in Repository
            classMetrics (bool): Enable or Disable calculation of ClassMetrics
            internalOntologyUrl (string): URL to cloned Repository stored locally on the machine
            remoteLocation (string): URL to the originating repository (For documentation purposes in DB)
            branch (string): Branch to work in
            repo (pygit2.Repository): the pygit2 Repository

        Returns:
            dict: Repository with evolutional ontology-Metrics
        """        
        #print(repo.url_str)                        
        # Read the index of the repo for accessing the files
        index = repo.index
        index.read()
        # Read the ID-representation of the requested File
        fileId = index[objectLocation].id
        # Access the RAW-Data of the File using its ID-representation as an identified      
        file = repo.get(fileId)
        opi = OpiHandler
        formerObj= None
        metricsDict = []
        # returnBuilder["branch"] = branch
        counter = 0
        # Iterates through the Repository, finding the Commits
        for commit in repo.walk(repo.head.target, Git.GIT_SORT_TIME | Git.GIT_SORT_REVERSE):
            obj = self.getFittingObject(objectLocation, commit.tree)
            if obj != None:
                if(formerObj != self.getFittingObject(objectLocation, commit.tree)):
                    counter += 1
                    formerObj = self.getFittingObject(objectLocation, commit.tree)
                    returnObject = {}
                    # Commit-Metadata
                    returnObject["CommitTime"] = datetime.fromtimestamp(commit.commit_time)
                    returnObject["CommitMessage"] = commit.message
                    returnObject["AuthorName"] = commit.author.name
                    returnObject["AuthorEmail"] = commit.author.email
                    returnObject["CommitterName"] = commit.committer.name
                    returnObject["CommiterEmail"] = commit.committer.email
                    returnObject.update(opi.opiOntologyRequest(obj.data, classMetrics=classMetrics))    
                    metricsDict.append(returnObject)
        
        # Write Metrics in Database
        sourceModel = Source.objects.create(fileName=objectLocation, repository=remoteLocation,branch=branch)
        for commitMetrics in metricsDict:
            metricsModel = Metrics.objects.create(CommitTime = commitMetrics["CommitTime"], metricSource=sourceModel)                
            modelDict = self.commit2MetricsModel(commitMetrics)
            metricsModel.__dict__.update(modelDict)
            metricsModel.save()
            if(classMetrics):
                for classMetricsValues in commitMetrics["OntologyMetrics"]["BaseMetrics"]["Classmetrics"]:
                    """classMetricsModel = classMetricsValues.objects.create(metric = metricsModel)
                    classMetricsModel.__dict__.update(classMetricsValues)
                    classMetricsModel.save()"""
                    
        
        return(metricsDict)

    def commit2MetricsModel(self, commitMetrics: dict) -> dict:
        """Flattens the nested Dict-Metrics to prepare it for a database write.

        Args:
            commitMetrics (dict): Calculated Ontology Metrics in nested structure

        Returns:
            dict: flattend Dict prepared for DB-write
        """
        tmpDict = {}
        tmpDict["CommitTime"] = commitMetrics["CommitTime"]
        tmpDict["CommitMessage"] = commitMetrics["CommitMessage"]
        tmpDict["AuthorName"] = commitMetrics["AuthorName"]
        tmpDict["AuthorEmail"] = commitMetrics["AuthorEmail"]
        tmpDict["CommitterName"] = commitMetrics["CommitterName"]
        tmpDict["CommiterEmail"] = commitMetrics["CommiterEmail"]
        tmpDict.update(commitMetrics["OntologyMetrics"]["BaseMetrics"]["Basemetrics"])
        tmpDict.update(commitMetrics["OntologyMetrics"]["BaseMetrics"]["Classaxioms"])
        tmpDict.update(commitMetrics["OntologyMetrics"]["BaseMetrics"]["Objectpropertyaxioms"])
        tmpDict.update(commitMetrics["OntologyMetrics"]["BaseMetrics"]["Datapropertyaxioms"])
        tmpDict.update(commitMetrics["OntologyMetrics"]["BaseMetrics"]["Individualaxioms"])
        tmpDict.update(commitMetrics["OntologyMetrics"]["BaseMetrics"]["Annotationaxioms"])
        tmpDict.update(commitMetrics["OntologyMetrics"]["BaseMetrics"]["Schemametrics"])
        tmpDict.update(commitMetrics["OntologyMetrics"]["BaseMetrics"]["Knowledgebasemetrics"])
        tmpDict.update(commitMetrics["OntologyMetrics"]["BaseMetrics"]["Graphmetrics"])
        return tmpDict
    
    def getFittingObject(self, searchObj, commitTree):
        """Finds specific File in git-commit-Object

        Args:
            searchObj ([type]): relative URL to findable Object
            commit ([type]): Commmit-Tree Object that shall be searched

        Returns:
            Object: Ontology-File
        """
              
        if type(searchObj) is list:
            searchList = searchObj
        else:
            searchList = searchObj.split("/")
        breadCrumbCounter = 0
        if(len(searchList) > 1):
            for breadCrumb in searchList:
            # Check if its the last element of the BreadCrumb Object
                if(commitTree.__contains__(breadCrumb)):
                    searchList.pop(0)
                    return(self.getFittingObject(searchList, commitTree.__getitem__(breadCrumb)))
                else:
                    return None
        else:
            if(commitTree.__contains__(searchList[0])):
                if(type(commitTree.__getitem__(searchList[0])) is Git.Tree):
                    self.getFittingObject(searchList[0], commitTree.__getitem__(searchList[0]))
                else:
                    return(commitTree.__getitem__(searchList[0]))