from ssl import ALERT_DESCRIPTION_PROTOCOL_VERSION
from django.http import HttpResponse, JsonResponse
import rest.metricOntologyHandler
from rest.CalculationManager import CalculationManager
from rest.opiHandler import OpiHandler
from django.shortcuts import render
from rest_framework.views import APIView
from ontoMetricsAPI.PlainTextParser import PlainTextParser
from rest_framework.response import Response
from rest_framework.request import Request
from rest_framework import viewsets
from rest_framework.exceptions import ParseError
import django_rq
import rest.queueInformation
import logging
from rest.CalculationHelper import GitHelper, GitUrlParser
from rest.dbHandler import DBHandler
from rest_framework.renderers import JSONRenderer
# from rest.models import Metrics, Source
# Create your views here.


class index(APIView):
    """Landing Page for REST-Service
    Returns:
        Response: HTML-Template "index.html"
    """
    renderer_classes = [JsonResponse]

    def get(self, request):
        return Response(template_name="index.html")


class CalculateMetric(APIView):
    def get(self, request, format=None):
        opi = OpiHandler
        url = request.GET.get("url")
        xmlDict = opi.opiUrlRequest(url)
        return(Response(xmlDict))


class MetricExplorer(APIView):
    # Calculating the Data for the Metric Explorer Frontent from the ontology is a quite extensive task, which
    # is not required for the worker applications (those who run and distribute the metric calculations).
    # The environment variable "isWorker" is set by the docker-compose file.

    # if not(bool(os.environ.get("isWorker", False))) and bool(os.environ.get("inDocker", False)):
    def get(self, request, format=None):
        explorer = rest.metricOntologyHandler.ontologyhandler.getMetricExplorer()
        return(Response(explorer))
class DownloadOntologyFile(APIView):
    def get(self, request, pk, format=None):
        file = CalculationManager().downloadSpecificOntologyFile(pk)
        for ontologyName, content in file.items():
            response = HttpResponse(content, content_type='text/plain')
            response['Content-Disposition'] = f'attachment; filename={ontologyName}'
            # response.write(file.values[0])
            return response



class MetricQueryViewSet(viewsets.ReadOnlyModelViewSet):
    def list(self, request):
        queryset = Source.objects.all()
        serializer = MetricSerializer(instance=queryset, many=True)
        return Response(serializer.data)

    def retrieve(self, request):
        #queryset = Source.objects.filter(repository=request)
        return(None)


class CalculateGitMetric(APIView):

    """Calculated the Ontology-Metrics for a Repository (Whole Repository or specific file within)

    Raises:
        ParseError: Wrong Ontology Formalizations
    """

    renderer_classes = [JSONRenderer]

    def get(self, request: Request, format=None):
        """Requests Ontology Metrics for a given URL (see: https://github.com/Uni-Rostock-Win/NEOntometrics/tree/master/Git-Extension).


        Args:
            request (Request): Contains the optional bool "ClassMetrics"-Value & the mandatory "url" value. 

        Raises:
            ParseError: Wrong input parameter delivered

        Returns:
            [type]: Ontology Metrics based on the given request (e.g. just single ontology, git-based history of an ontology or whole for all ontologies in git-repository)
        """
        targetLocation = ""
        url = GitUrlParser()
        queueInfo = rest.queueInformation.QueueInformation(
            request.query_params["url"])

        try:
            if "url" in request.query_params:
                url.parse(request.query_params["url"])
            else:
                raise ParseError("Wrong Input parameter! Check Documentation")
            classMetrics = GitHelper.str2bool(
                request.query_params["classMetrics"]) if "classMetrics" in request.query_params else False
            hideId = GitHelper.str2bool(
                request.query_params["hideId"]) if "hideId" in request.query_params else True
            reasonerSelected = GitHelper.str2bool(
                request.query_params["reasoner"]) if "reasoner" in request.query_params else False

            branch = request.query_params["branch"] if "branch" in request.query_params else None
        except KeyError as identifier:
            print(identifier)
            raise ParseError("Wrong Input parameter! Check Documentation")

        # At first check if the value is already stored in the database
        db = DBHandler()
        metricFromDB = db.getMetricForOntology(
            file=url.file, repository=url.repository, reasonerSelected=reasonerSelected, classMetrics=classMetrics, hideId=hideId)
        if(metricFromDB):
            logging.debug("Read Metrics from Database")
            return Response(metricFromDB)

        # If it is not already in the database, check the queue
        # The JobId is the ID of the task whithin the queue
        jobId = GitHelper.serializeJobId(
            url)
        if jobId in django_rq.get_queue().job_ids:
            resp = queueInfo.getQueueAnswer(url, jobId)
            return Response(resp)
        # If it is  not in the queue, it might be in failed? Check the failed job registry..
        elif jobId in django_rq.get_queue().failed_job_registry:
            job = django_rq.get_queue().fetch_job(jobId)
            if ("status code: 404" in job.exc_info) or ("KeyError: \"reference" in job.exc_info):
                return Response({
                    "status": 400,
                    "url": GitHelper.deserializeJobId(jobId),
                    "info": "No valid Ontology or Git-Repository found at this URL. Check your Query!"
                }, status=400, exception=True)
            else:
                return Response({
                    "status": 500,
                    "url": GitHelper.deserializeJobId(jobId),
                    "info": "internal server error"
                }, status=500)
        logging.debug("Put job in queue")

        calculationManager = CalculationManager()
        calculationManager.putInQueue(url=url, classMetrics=classMetrics,
                        reasonerSelected=reasonerSelected)
        return Response(queueInfo.getQueueAnswer(url, jobId))

    def delete(self, request: Request, format=None):
        """Delete a metric caluclation from the Database

        Args:
            request (Request): url containing the target repo or file
        """
        targetLocation = ""
        url = GitUrlParser()
        try:
            if "url" in request.query_params:
                request.query_params["url"]
                url.parse(request.query_params["url"])
        except KeyError as identifier:
            print(identifier)
            raise ParseError("Wrong Input parameter! Check Documentation")
        db = DBHandler()
        metricFromDB = db.getMetricForOntology(
            file=url.file, repository=url.repository)
        if (metricFromDB):
            db.deleteMetric(file=url.file, repository=url.repository)
        else:
            raise ParseError("No fitting ontology in Database")
        resp = {
            "deleted": True,
        }
        resp.update(url.__dict__)
        return(Response(resp))

    parser_classes = [PlainTextParser]
    authentication_classes = []

    def post(self, request: Request, format=None):
        data = request.body.decode()
        opi = OpiHandler()
        metric = opi.opiOntologyRequest(data, True)
        return(Response(metric))
