from django.http.request import RAISE_ERROR
from rest.models import Metrics, Source, ClassMetrics
from collections import OrderedDict
import logging

class DBHandler:

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
        sourceModel = Source.objects.create(fileName=file, repository=repo,branch=branch, wholeRepositoryAnalyzed= wholeRepo)
        for commitMetrics in metricsDict:
            metricsModel = Metrics.objects.create(CommitTime = commitMetrics["CommitTime"], metricSource=sourceModel)                
            modelDict = self.__commit2MetricsModel__(commitMetrics)
            metricsModel.__dict__.update(modelDict)
            metricsModel.save()
            if "Classmetrics" in commitMetrics["OntologyMetrics"]["BaseMetrics"]:
                for classMetricsValues in commitMetrics["OntologyMetrics"]["BaseMetrics"]["Classmetrics"]:
                    classMetricsModel = ClassMetrics.objects.create(metric = metricsModel)
                    classMetricsModel.__dict__.update(classMetricsValues)
                    classMetricsModel.save()
        return sourceModel

    def getMetricForOntology(self, repository: str,file ="", branch = "master", classMetrics=False) -> dict:
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
        for SourceRepo in sourceData:
            returnDict =  OrderedDict()
            metricsDict = OrderedDict()
            metricsData = Metrics.objects.filter(metricSource=SourceRepo)
            queryMetaData = sourceData.values()[0]
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
                    data.update({"Classmetrics": classMetricList})
                metricsDataToDict.append(data)
            returnDict.update({"metrics": metricsDataToDict})
            metricsArray.append(returnDict)
        
        return metricsArray
    def deleteMetric(self, repository, file=""):
        if(file == ""):
            repoMetrics = Source.objects.filter(repository = repository)
        else:
            repoMetrics = Source.objects.filter(repository = repository, file=file)
        repoMetrics.delete()
    def setWholeRepoAnalyzed(self, repository: str):
        """Marks that all files in a repository are analyzed

        Args:
            repository (str): url to Repository
        """
        repoMetrics = Source.objects.filter(repository = repository)
        repoMetrics.update(wholeRepositoryAnalyzed=True)