import django
import pygit2 as Git
from os import path
from shutil import rmtree
import logging
import copy, requests
from datetime import datetime
from rest.GitHelper import GitHelper, GitUrlParser
from rest.opiHandler import OpiHandler
from rq import job
from django.conf import settings
from rest.dbHandler import DBHandler
import django_rq
import rq


class CalculationManager:
    """Handles the all preparation before sending the data to the OPI-Endpoint
    """
    logger = logging.getLogger(__name__)
    logging.basicConfig(level=logging.DEBUG)

    @django_rq.job
    def ontologyFileWORepo(self, url: GitUrlParser, reasonerSelected: bool):
        """Analyze a single ontology file that is not bound to a repository

        Args:
            url (GitHelper.GitUrlParser): The GitHelper object that contains information regarding the ontoloy file source
            reasonerSelected (bool): Depicts if the metrics are calculated using a reasoner or not.
        """
        db = DBHandler()
        getOntologyResponse = requests.get("http://" + url.file)
        ontology = getOntologyResponse.text.replace("\n", "")
        opi = OpiHandler()
        metrics = opi.opiOntologyRequest(
            ontologyString=ontology, classMetrics=False, ontologySize=len(getOntologyResponse.content), reasoner=reasonerSelected)
        db.writeInDB(file=url.file, repo=url.repository,
                     metricsDict=metrics, wholeRepo=0)

    @django_rq.job
    def getObject(self, repositoryUrl: str, objectLocation: str, branch=None, reasoner: bool = False, classMetrics=False) -> dict:
        """Analyses one ontology-file for evolutional ontology metrics

        Args:
            repositoryUrl (string): URL to remote Repository
            objectLocation (string): relative path to targeted ontology file in Repository
            branch (str, optional): selected branch. Defaults to "None".
            classMetrics (bool, optional): Enables the calculation of Class Metrics (Computational Expensive). Defaults to False.
            reasoner (bool, optional): selects that the calcualtion engine runs on a reasoned ontology.

        Returns:
            dict: Evolutional Ontology Metrics
        """

        # Creates a folder for the git-Repository based on the hash of the repository URL.
        internalOntologyUrl = "ontologies/" + str(hash(repositoryUrl))
        if(path.exists(internalOntologyUrl) == False):
            Git.clone_repository("https://" + repositoryUrl,
                                 internalOntologyUrl, checkout_branch=branch)
            self.logger.debug("Repository cloned at " + internalOntologyUrl)

        repo = Git.Repository(internalOntologyUrl)
        metrics = self.getOntologyMetrics(
            objectLocation, classMetrics, reasoner, internalOntologyUrl, repositoryUrl, branch, repo)
        rmtree(internalOntologyUrl, ignore_errors=True)
        return(True)

    @django_rq.job
    def getObjects(self, repositoryUrl: str,  branch=None, classMetrics=False, reasoner=False) -> dict:
        """Analysis all ontology files in a git Repository

        Args:
            repositoryUrl (string): URL to remote Repository
            objectLocation (string): relative path to targeted ontology file in Repository
            branch (str, optional): selected branch. Defaults to "None".
            classMetrics (bool, optional): Enables the calculation of Class Metrics (Computational Expensive). Defaults to False.

        Returns:
            dict: Evolutional Ontology Metrics
        """

        # Creates a folder for the git-Repository based on the hash of the repository URL.
        internalOntologyUrl = "ontologies/" + str(hash(repositoryUrl))
        if(path.exists(internalOntologyUrl) == False):
            Git.clone_repository("http://" + repositoryUrl,
                                 internalOntologyUrl, checkout_branch=branch)
            self.logger.debug("Repository cloned at " + internalOntologyUrl)
        repo = Git.Repository(internalOntologyUrl)
        index = repo.index
        index.read()
        metrics = []

        # This part is for counting how much ontologies are to be calculated in this repository
        currentJob = rq.job.get_current_job()
        ontologiesToBeAnalyzed = 0
        analyzedOntologies = 0
        for item in index:
            if(item.path.endswith((".ttl", ".owl", ".rdf"))):
                ontologiesToBeAnalyzed += 1
        currentJob.meta = {"analysableOntologies": ontologiesToBeAnalyzed,
                           "analyzedOntologies": analyzedOntologies}
        currentJob.save_meta()

        for item in index:
            if(item.path.endswith((".ttl", ".owl", ".rdf"))):
                self.logger.debug("Analyse Ontology: "+item.path)
                logging.debug("Analyse Ontology: "+item.path)
                if(reasoner):
                    metrics.append(self.getOntologyMetrics(objectLocation=item.path, classMetrics=classMetrics, reasoner=False,
                                   internalOntologyUrl=internalOntologyUrl, remoteLocation=repositoryUrl, branch=branch, repo=repo))
                    metrics.append(self.getOntologyMetrics(objectLocation=item.path, classMetrics=classMetrics, reasoner=True,
                                   internalOntologyUrl=internalOntologyUrl, remoteLocation=repositoryUrl, branch=branch, repo=repo))

                else:
                    metrics.append(self.getOntologyMetrics(objectLocation=item.path, classMetrics=classMetrics, reasoner=False,
                                   internalOntologyUrl=internalOntologyUrl, remoteLocation=repositoryUrl, branch=branch, repo=repo))

                analyzedOntologies += 1
                currentJob.meta = {
                    "analysableOntologies": ontologiesToBeAnalyzed, "analyzedOntologies": analyzedOntologies}
                currentJob.save_meta()

        dbhandler = DBHandler()
        dbhandler.setWholeRepoAnalyzed(repository=repositoryUrl)
        rmtree(internalOntologyUrl, ignore_errors=True)
        return(True)

    def getOntologyMetrics(self, objectLocation: str, classMetrics: bool, reasoner: bool, internalOntologyUrl: str, remoteLocation: str, branch: str, repo: Git.Repository) -> dict:
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

        # Read the index of the repo for accessing the files
        index = repo.index
        index.read()
        # Read the ID-representation of the requested File
        fileId = index[objectLocation].id
        # Access the RAW-Data of the File using its ID-representation as an identified
        file = repo.get(fileId)
        opi = OpiHandler()
        formerObj = None
        metricsDict = []
        commitList = []
        # Iterates through the Repository, finding the relevant Commits based on paths
        for commit in repo.walk(repo.head.target, Git.GIT_SORT_TOPOLOGICAL | Git.GIT_SORT_REVERSE):
            obj = self._getFittingObject(objectLocation, commit.tree)
            if obj != None:
                if(formerObj != obj):
                    formerObj = obj
                    commitList.append(commit)

        # Sorting the Commits based on TIME, not on PARENTS, thus making the list ready for further filtering
        commitList.sort(
            key=lambda commit: datetime.fromtimestamp(commit.commit_time))
        formerObj = None
        commitCounter = 0
        for commit in commitList:
            commitCounter += 1
            job = rq.job.get_current_job()
            job.meta.update({
                "commitsForThisOntology": len(commitList),
                "ananlyzedCommits": commitCounter
            })
            job.save_meta()

            obj = self._getFittingObject(objectLocation, commit.tree)
            if obj != None:
                if(formerObj != obj):
                    branches = repo.branches.with_commit(commit)
                    formerObj = obj
                    returnObject = {}
                    # Commit-Metadata 
                    returnObject["CommitTime"] = datetime.fromtimestamp(
                        commit.commit_time)
                    returnObject["CommitMessage"] = commit.message
                    returnObject["AuthorName"] = commit.author.name
                    returnObject["AuthorEmail"] = commit.author.email
                    returnObject["CommitterName"] = commit.committer.name
                    returnObject["CommiterEmail"] = commit.committer.email
                    
                    self.logger.debug(
                        "Date: " + str(returnObject["CommitTime"]))
                    self.logger.debug("Commit:" + commit.message)
                    try:
                        opiMetrics = opi.opiOntologyRequest(
                            obj.data, ontologySize=obj.size, classMetrics=classMetrics, reasoner=reasoner)
                        returnObject.update(opiMetrics)
                        self.logger.debug("Ontology Analyzed Successfully")
                    except IOError:
                        # A reading Error occurs, e.g., if an ontology does not conform to a definied ontology standard and cannot be parsed
                        self.logger.warning(
                            "Ontology {0} not Readable ".format(obj.name))
                        returnObject["ReadingError"] = "Ontology not Readable"
                    metricsDict.append(returnObject)
        # Write Metrics in Database
        dbhandler = DBHandler()
        dbhandler.writeInDB(metricsDict, branch=branch,
                            file=objectLocation, repo=remoteLocation)
        return(metricsDict)

    def _getFittingObject(self, searchObj, commitTree):
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
                    return(self._getFittingObject(searchList, commitTree.__getitem__(breadCrumb)))
                else:
                    return None
        else:
            if(commitTree.__contains__(searchList[0])):
                if(type(commitTree.__getitem__(searchList[0])) is Git.Tree):
                    self._getFittingObject(
                        searchList[0], commitTree.__getitem__(searchList[0]))
                else:
                    return(commitTree.__getitem__(searchList[0]))
