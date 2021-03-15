from rest.git import GitHandler
from rest.opiHandler import OpiHandler
from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.request import Request
from rest_framework.exceptions import APIException, ParseError
import django_rq, rq, redis, logging
from rest.GitHelper import GitHelper, GitUrlParser
from rest.DBHandler import DBHandler
# Create your views here.

class CalculateMetric(APIView):
    def get(self, request, format=None):
        opi = OpiHandler
        url = request.GET.get("url")
        xmlDict = opi.opiUrlRequest(url)
        return(Response(xmlDict))


class CalculateGitMetric(APIView):
    """Calculated the Ontology-Metrics for a Repository (Whole Repository or specific file within)

    Raises:
        ParseError: Wrong Ontology Formalizations
    """     
    def get(self, request: Request, format=None):
        targetLocation = ""
        url = GitUrlParser()
        
        try:
            if "url" in request.query_params:
                request.query_params["url"]
                url.parse(request.query_params["url"])
            if "classMetrics" in request.query_params:
                classMetrics = GitHelper.str2bool(request.query_params["classMetrics"])
            else:
                classMetrics = False
            if "branch" in request.query_params:
                branch = request.query_params["branch"]
            else:
                branch = "master"
        except KeyError as identifier:
            print(identifier)
            raise ParseError("Wrong Input parameter! Check Documentation")
        # At first check if the value is already stored in the database
        db = DBHandler()
        metricFromDB = db.getMetricForOntology(file=url.file, repository=url.repository, classMetrics=classMetrics)
        if(metricFromDB):
            logging.debug("Read Metrics from Database")
            return Response(metricFromDB)
        
        # If it is not already in the database, check the queue
        # The JobId is the ID of the task whithin the queue
        jobId = GitHelper.serializeJobId(url.url)
        if jobId in django_rq.get_queue().job_ids:
            resp = self.__getQueueAnswer__(url, jobId)          
            return Response(resp)

        logging.debug("Put job in queue")
        gitHandler = GitHandler()
        if url.file != '':
            metrics = django_rq.enqueue(gitHandler.getObject, repositoryUrl=url.repository, objectLocation=url.file, branch=url.branch, classMetrics=classMetrics, job_id=jobId)       
        else:
            metrics = django_rq.enqueue(gitHandler.getObjects, repositoryUrl= url.repository, classMetrics=classMetrics, job_id=jobId)            
        return Response(self.__getQueueAnswer__(url, jobId))

    def delete(self, request: Request, format=None):
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
        metricFromDB = db.getMetricForOntology(file=url.file, repository=url.repository)
        if (metricFromDB):
            db.deleteMetric(file=url.file, repository=url.repository)
        else:
            raise ParseError("No fitting ontology in Database")
        resp = {
            "deleted": True,
        }
        resp.update(url.__dict__)
        return(resp)
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
            "taskedFinished": False,
            "queuePosition": jobPosition if jobPosition != None else 0}
        resp.update(url.__dict__)
        return resp