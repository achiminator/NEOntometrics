from django.http import JsonResponse
from rest.metricOntologyHandler import OntologyHandler
from rest.models import ClassMetrics
from rest_framework.renderers import TemplateHTMLRenderer
from rest.git import GitHandler
from rest.opiHandler import OpiHandler
from django.shortcuts import render
from rest_framework.views import APIView
from ontoMetricsAPI.PlainTextParser import PlainTextParser
from rest_framework.response import Response
from rest_framework.request import Request
from rest_framework.exceptions import APIException, ParseError
import django_rq
import rq
import redis
import logging
import requests
import django
import os
from braces.views import CsrfExemptMixin
from rest.GitHelper import GitHelper, GitUrlParser
from rest.dbHandler import DBHandler
from rest_framework.renderers import JSONRenderer
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
    if not(bool(os.environ.get("isWorker", False))):
        ontology = OntologyHandler()

        def get(self, request, format=None):
            explorer = self.ontology.getMetricExplorer()
            return(Response(explorer))


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

        # Analyze a Single Ontology File
        # If the url.repository is empty, no GIT-repository is given and the user pointed directly to a single ontology File
        if(url.repository == ""):
            getOntologyResponse = requests.get("http://" + url.file)
            ontology = getOntologyResponse.text.replace("\n", "")
            opi = OpiHandler()
            metrics = opi.opiOntologyRequest(
                ontology, False, reasoner=reasonerSelected)
            db.writeInDB(file=url.file, repo=url.repository,
                            metricsDict=metrics, wholeRepo=0)
            
                # raise ParseError("No Valid Ontology is given in the URL")
            return(Response(metrics))

        # If it is not already in the database, check the queue
        # The JobId is the ID of the task whithin the queue
        jobId = GitHelper.serializeJobId(url.url)
        if jobId in django_rq.get_queue().job_ids:
            resp = self.__getQueueAnswer__(url, jobId)
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
        gitHandler = GitHandler()
        if url.file != '':
            metrics = django_rq.enqueue(gitHandler.getObject, repositoryUrl=url.repository, objectLocation=url.file,
                                        branch=url.branch, classMetrics=classMetrics, reasoner=reasonerSelected,  job_id=jobId)
        # If no file is given, analyze the whole REPO
        else:
            metrics = django_rq.enqueue(gitHandler.getObjects, repositoryUrl=url.repository,
                                        classMetrics=classMetrics, reasoner=reasonerSelected, job_id=jobId)
        return Response(self.__getQueueAnswer__(url, jobId))

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

    def __getQueueAnswer__(self, url: GitUrlParser, jobId: str) -> dict:
        """Generates the response if the ontology to calculate is not yet finished

        Args:
            url (GitUrlParser): The GitUrlParser-Object containing information on the target repository/file
            jobId (str): The Job-ID/Request URL

        Returns:
            dict: Output Dict ready for JSON-Serialization
        """
        logging.debug("Job" + url.url + " is already in Queue")
        redis_conn = django_rq.get_connection()
        job = django_rq.jobs.Job(jobId, redis_conn)

        jobPosition = django_rq.get_queue().get_job_position(job)
        resp = {
            "taskFinished": False,
            "taskIsStarted": job in django_rq.get_queue().started_job_registry,
            "queuePosition": jobPosition if jobPosition != None else 0,
            "progress": job.get_meta()
        }
        resp.update(url.__dict__)
        return resp

    def __getFailedQueueAnswer__(self, jobId: str) -> dict:
        job = django_rq.get_queue().fetch_job(jobId)
        resp = {
            "status": 400,
            "url": GitHelper.deserializeJobId(jobId),
            "info": "No valid Ontology or Git-Repository found at this URL. Check your Query!"
        }
