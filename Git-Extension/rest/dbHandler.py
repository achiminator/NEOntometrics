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
            a = commitMetrics["ReadingError"]
            if not commitMetrics["ReadingError"]:
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
    def writeInDB(self, metricsDict: dict, branch: str, file: str, repo: str, wholeRepo=False) -> Source:
        """Writes calculated metric-Values in Database

        Args:
            metricsDict (dict): Dictionary containing the metric-values
            branch (str): analzed banch
            file (str): Relative path to ontology within repository
            repo (str): Repository URL
            wholeRepo(bool): Stores if the whole repository was analyzed or not
        """

        # At first, check if these elements already exits. If yes, delete them.
        results =  Source.objects.filter(fileName=file, repository=repo,branch=branch).delete()
        
        sourceModel = Source.objects.create(fileName=file, repository=repo,branch=branch, wholeRepositoryAnalyzed= wholeRepo)
        for commitMetrics in metricsDict:
            metricsModel = Metrics.objects.create(CommitTime = commitMetrics["CommitTime"], metricSource=sourceModel)                
            modelDict = self.__commit2MetricsModel__(commitMetrics)
            metricsModel.__dict__.update(modelDict)
            metricsModel.save()
            if "OntologyMetrics" in commitMetrics:
                if "Classmetrics" in commitMetrics["OntologyMetrics"]["BaseMetrics"]:
                    for classMetricsValues in commitMetrics["OntologyMetrics"]["BaseMetrics"]["Classmetrics"]:
                        classMetricsModel = ClassMetrics.objects.create(metric = metricsModel)
                        classMetricsModel.__dict__.update(classMetricsValues)
                        classMetricsModel.save()
        return sourceModel

    def getMetricForOntology(self, repository: str,file ="", branch = "master", classMetrics=False) -> dict:
        """Retrieves Metric Calculation (for one ontology or whole Repo) from the database. 

        Args:
            repository (str): URL to Repository and service (e.g. github.com/ESIPFed/sweet/).
            file (str, optional): URL to the target file within the repository (e.g. src/human.ttl). Defaults to "". If left empty, the whole repo will be analysed
            branch (str, optional): branch to analyse. Defaults to "master".
            classMetrics (bool, optional): If the class-metrics shall be retrieved as well. Defaults to False.

        Raises:
            Exception: Inconsistend Data in the DB
        Returns:
            dict: Array with the metrics. If no Metrics are found, the method returns an empty Dict {}
        """
        if(file == ""):
            sourceData = Source.objects.filter(repository=repository, branch=branch, wholeRepositoryAnalyzed=1)
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
            metricsData = Metrics.objects.filter(metricSource=SourceRepo)
            queryMetaData = sourceData.values()[iterator]
            iterator += 1
            queryMetaData.pop("id")
            returnDict.update(queryMetaData)
            metricsDataToDict =[]
            for commitMetrics in metricsData.values():
                data = OrderedDict()
                # This ugly piece of code stores the first items in the dict (without db's foreign/primary keys )
                data.update(OrderedDict(list(commitMetrics.items())[2:8]))
                data.update({"OntologyMetrics":
                {
                "BaseMetrics":   OrderedDict(list(commitMetrics.items())[9:20]),
                "ClassAxioms":   OrderedDict(list(commitMetrics.items())[21:25]),
                "Objectpropertyaxioms":   OrderedDict(list(commitMetrics.items())[26:40]),
                "Datapropertyaxioms":   OrderedDict(list(commitMetrics.items())[41:46]),
                "Individualaxioms":   OrderedDict(list(commitMetrics.items())[47:54]),
                "Annotationaxioms":   OrderedDict(list(commitMetrics.items())[55:59]),
                "Schemametrics":   OrderedDict(list(commitMetrics.items())[60:69]),
                "Knowledgebasemetrics":   OrderedDict(list(commitMetrics.items())[69:71]),
                "Graphmetrics":   OrderedDict(list(commitMetrics.items())[72:86]),
                }})                
                if(classMetrics):
                    classMetricList = []
                    for classMetric in ClassMetrics.objects.filter(metric=commitMetrics["id"]).values(): 
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
            repoMetrics = Source.objects.filter(repository = repository)
        else:
            repoMetrics = Source.objects.filter(repository = repository, fileName=file)
        repoMetrics.delete()
    def setWholeRepoAnalyzed(self, repository: str):
        """Marks that all files in a repository are analyzed

        Args:
            repository (str): url to Repository
        """
        repoMetrics = Source.objects.filter(repository = repository)
        repoMetrics.update(wholeRepositoryAnalyzed=True)