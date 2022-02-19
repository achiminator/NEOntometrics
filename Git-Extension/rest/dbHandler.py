from django.http.request import RAISE_ERROR
from rest.models import Metrics, Source, ClassMetrics
from collections import OrderedDict
import logging

class DBHandler:
    """Handles the Database connections, especially prepares the metrics for writing into the database.
    """
    def __commit2MetricsModel__(self, commitMetrics: dict) -> dict:
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
            tmpDict["ReadingError"] = commitMetrics["ReadingError"]
            tmpDict["Size"] = commitMetrics["Size"] 
            if not commitMetrics["ReadingError"]:
                tmpDict.update(commitMetrics["OntologyMetrics"]["GeneralOntologyMetrics"])
            return tmpDict
    def writeInDB(self, metricsDict: dict, file: str, repo: str, wholeRepo=False, branch: str="") -> Source:
        """Writes calculated metric-Values in Database

        Args:
            metricsDict (dict): Dictionary containing the metric-values
            branch (str): analzed banch
            file (str): Relative path to ontology within repository
            repo (str): Repository URL
            wholeRepo(bool): Stores if the whole repository was analyzed or not
        """
        if(branch == None):
            branch = ""
        # At first, check if these elements already exits. If yes, delete them.
        results =  Source.objects.filter(fileName=file, repository=repo,branch=branch).delete()
        sourceModel = Source.objects.create(fileName=file, repository=repo,branch=branch, wholeRepositoryAnalyzed= wholeRepo)
        for commitMetrics in metricsDict:
            # This statement is a little bit "rough". However, if the iteration on metricsDict is a string,
            # then we analyzed a single ontology metric (then have not analyzed a Repository) and do not need to run the "@commit2Metrics" method.
            if(type(commitMetrics) == str):
                metricsModel = Metrics.objects.create(metricSource=sourceModel) 
                modelDict = metricsDict["OntologyMetrics"]["GeneralOntologyMetrics"]
            else:
                metricsModel = Metrics.objects.create(CommitTime = commitMetrics["CommitTime"], metricSource=sourceModel)                
                modelDict = self.__commit2MetricsModel__(commitMetrics)
            metricsModel.__dict__.update(modelDict)
            metricsModel.save()
            # if "OntologyMetrics" in commitMetrics:
            #     if "Classmetrics" in commitMetrics["OntologyMetrics"]:
            #         for classMetricsValues in commitMetrics["OntologyMetrics"]["BaseMetrics"]["Classmetrics"]:
            #             classMetricsModel = ClassMetrics.objects.create(metric = metricsModel)
            #             classMetricsModel.__dict__.update(classMetricsValues)
            #             classMetricsModel.save()
        return sourceModel

    def getMetricForOntology(self, repository: str,file ="", reasonerSelected: bool=False, branch = "", classMetrics=False, hideId=True) -> dict:
        """Retrieves Metric Calculation (for one ontology or whole Repo) from the database. 

        Args:
            repository (str): URL to Repository and service (e.g. github.com/ESIPFed/sweet/).
            file (str, optional): URL to the target file within the repository (e.g. src/human.ttl). Defaults to "". If left empty, the whole repo will be analysed
            branch (str, optional): branch to analyse. Defaults to "master".
            classMetrics (bool, optional): If the class-metrics shall be retrieved as well. Defaults to False.
            hideId(bool, optional): Hides the ID of the Database Entry for further identification

        Raises:
            Exception: Inconsistend Data in the DB
        Returns:
            dict: Array with the metrics. If no Metrics are found, the method returns an empty Dict {}
        """

        if(file == ""):
            sourceData = Source.objects.filter(repository=repository, wholeRepositoryAnalyzed=1)
        else:
            sourceData = Source.objects.filter(repository=repository, fileName=file, branch=branch)
        if file != "" and len(sourceData.values()) > 1:
            logging.critical("More than one Calculation for an Ontology already in the DB")
            raise Exception("To much fitting Data in DB")
        elif len(sourceData.values())  == 0:
            return {}
        metricsArray = []
        iterator = 0
        for SourceRepo in sourceData:
            returnDict =  OrderedDict()
            metricsDict = OrderedDict()
            metricsData = Metrics.objects.filter(metricSource=SourceRepo, reasonerActive =reasonerSelected)
            queryMetaData = sourceData.values()[iterator]
            iterator += 1
            if hideId: queryMetaData.pop("id")
            returnDict.update(queryMetaData)
            metricsDataToDict =[]
            for commitMetrics in metricsData.values():
                data = OrderedDict()
                # This ugly piece of code stores the first items in the dict (without db's foreign/primary keys )
                data.update(OrderedDict(list(commitMetrics.items())))             
                if(classMetrics):
                    classMetricList = []
                    for classMetric in ClassMetrics.objects.filter(metric=commitMetrics["id"]).values(): 
                        if hideId:
                            classMetric.pop("metric_id")
                            classMetric.pop("id")
                        classMetricList.append(classMetric)
                        
                        # If no Classmetrics where found but were requested, return 0 (and trigger new calculation)
                    if(len(classMetricList) < 1):
                        return None
                    data.update({"Classmetrics": classMetricList})
                metricsDataToDict.append(data)
            returnDict.update({"metrics": metricsDataToDict})
            metricsArray.append(returnDict)
        
        return metricsArray
    def deleteMetric(self, repository: str, file=""):
        """Deletes the metrics from the DB

        Args:
            repository (str): URL to Repository and service (e.g. github.com/ESIPFed/sweet/).
            file (str, optional): URL to the target file within the repository (e.g. src/human.ttl). Defaults to "". If left empty, the whole repo will be deleted
        """
        if(file == ""):
            Source.objects.filter(repository = repository).delete()
        else:
            Source.objects.filter(repository = repository, fileName=file).delete()
            Source.objects.filter(repository = repository).update(wholeRepositoryAnalyzed=False)

    def setWholeRepoAnalyzed(self, repository: str):
        """Marks that all files in a repository are analyzed

        Args:
            repository (str): url to Repository
        """
        repoMetrics = Source.objects.filter(repository = repository)
        repoMetrics.update(wholeRepositoryAnalyzed=True)