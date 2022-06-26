from django.conf import settings
from django.http.request import RAISE_ERROR
from rest.CalculationHelper import GitUrlParser
from rest.models import Commit, Repository, OntologyFile, ClassMetrics
from collections import OrderedDict

from django.db.models import Q
import logging, django_filters


class DBHandler:
    """Handles the Database connections, especially prepares the metrics for writing into the database.
    """

    #@DeprecationWarning
    def getMetricForOntology(self, repository: str, file="", reasonerSelected: bool = False, branch="", classMetrics=False, hideId=True) -> dict:
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
            sourceData = Repository.objects.filter(
                repository=repository, wholeRepositoryAnalyzed=1)
        else:
            sourceData = Repository.objects.filter(
                repository=repository, fileName=file, branch=branch)
        if file != "" and len(sourceData.values()) > 1:
            logging.critical(
                "More than one Calculation for an Ontology already in the DB")
            raise Exception("To much fitting Data in DB")
        elif len(sourceData.values()) == 0:
            return {}
        metricsArray = []
        iterator = 0
        for SourceRepo in sourceData:
            returnDict = OrderedDict()
            metricsDict = OrderedDict()
            # If the size is larger than the reasoning limit, the reasoner will always be false.
            # Thus, querying with the input parameter will just return empty values. This complex query
            # avoids this problem and returns also metrics with deactivated reasoner.
            metricsLargerThanReasoningLimit = Q(
                Size__gt=settings.REASONINGLIMIT) & Q(reasonerActive__exact=False)
            metricsSmallerThanReasoningLimit = Q(Size__lte=settings.REASONINGLIMIT) & Q(
                reasonerActive__exact=reasonerSelected)
            metricsAssociatedWithOntology = Q(metricSource=SourceRepo)
            metricsData = Metrics.objects.filter(metricsAssociatedWithOntology & (
                metricsLargerThanReasoningLimit | metricsSmallerThanReasoningLimit))
            #metricsData = Metrics.objects.filter(metricSource=SourceRepo, reasonerActive =reasonerSelected)
            queryMetaData = sourceData.values()[iterator]
            iterator += 1
            if hideId:
                queryMetaData.pop("id")
            returnDict.update(queryMetaData)
            metricsDataToDict = []
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
            Source.objects.filter(repository=repository).delete()
        else:
            Source.objects.filter(repository=repository,
                                  fileName=file).delete()
            Source.objects.filter(repository=repository).update(
                wholeRepositoryAnalyzed=False)


class RepositoryFilter(django_filters.FilterSet):
    """A custom django-repository filter. Filters the Repository for the filenames and Repository names that are part of a git-url

    Args:
        django_filters (_type_): 

    Returns:
        django_filter.queryset.filter: A django-filter set for filtering django Models
"""
    repository = django_filters.CharFilter(field_name="repository", method="repoFilter", required=True)
    fileName = django_filters.CharFilter(field_name="fileName", method="fileFilter")
    

    def repoFilter(self, queryset, name, value):
        urlObject = GitUrlParser()
        urlObject.parse(value)
        return queryset.filter(repository = urlObject.repository)
        
    def fileFilter(self, queryset, name, value):
        urlObject = GitUrlParser()
        urlObject.parse(value)
        return queryset.filter(fileName = urlObject.file)