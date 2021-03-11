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
    def writeInDB(self, metricsDict: dict, branch: str, file: str, repo: str):
        """Writes calculated metric-Values in Database

        Args:
            metricsDict (dict): Dictionary containing the metric-values
            branch (str): analzed banch
            file (str): Relative path to ontology within repository
            repo (str): Repository URL
        """
        sourceModel = Source.objects.create(fileName=file, repository=repo,branch=branch)
        for commitMetrics in metricsDict:
            metricsModel = Metrics.objects.create(CommitTime = commitMetrics["CommitTime"], metricSource=sourceModel)                
            modelDict = self.__commit2MetricsModel__(commitMetrics)
            metricsModel.__dict__.update(modelDict)
            metricsModel.save()
            if(commitMetrics["OntologyMetrics"]["BaseMetrics"]["Classmetrics"]):
                for classMetricsValues in commitMetrics["OntologyMetrics"]["BaseMetrics"]["Classmetrics"]:
                    classMetricsModel = ClassMetrics.objects.create(metric = metricsModel)
                    classMetricsModel.__dict__.update(classMetricsValues)
                    classMetricsModel.save()

    def getMetricForOntology(self, file: str, repository: str, branch = "master") -> dict:
        returnDict =  OrderedDict()
        metricsDict = OrderedDict()
        sourceData = Source.objects.filter(repository=repository, fileName=file, branch=branch)
        if len(sourceData.values()) > 1:
            logging.critical("More than one Calculation for an Ontology already in the DB")
            raise Exception("To much fitting Data in DB")
        elif len(sourceData.values())  == 0:
            return {}
        metricsData = Metrics.objects.filter(metricSource=sourceData[0])
        returnDict.update(sourceData.values()[0])
        metricsDataToDict =[]
        for commitMetrics in metricsData.values():
            data = OrderedDict()
            # This ugly piece of code does store the first items in the dict
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
             #"Classmetrics": 
            classMetricList = []
            for classMetric in ClassMetrics.objects.filter(metric=commitMetrics["id"]).values(): 
                classMetricList.append(classMetric)
            data.update({"Classmetrics": classMetricList})
            metricsDataToDict.append(data)
        returnDict.update({"metrics": metricsDataToDict})
        return returnDict