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
        metricFromDB = db.getMetricForOntology(file=url.file, repository=url.repository)
        if(metricFromDB):
            logging.debug("Read Metrics from Database")
            return Response(metricFromDB)
        
        # If it is not already in the database, check the queue
        if GitHelper.serializeJobId(url.url) in django_rq.get_queue():
            logging.debug("Job is already in Queue")
            return Response("In Queue!")

        logging.debug("Put job in queue")
        gitHandler = GitHandler()
        if url.file != '':
            metrics = django_rq.enqueue(gitHandler.getObject, repositoryUrl=url.repository, objectLocation=url.file, branch=url.branch, classMetrics=classMetrics, job_id=GitHelper.serializeJobId(url.url))       
        else:
            metrics = django_rq.enqueue(gitHandler.getObjects, repositoryUrl=url.repository, branch=branch, classMetrics=classMetrics, job_id=GitHelper.serializeJobId(url.url))            
        return(Response(GitHelper.deserializeJobId(str(metrics.id))))

class CalculatedMetrics(APIView):
    
    def get(self, request: Request, format=None):
        pass
    #     returnBuilder = {}
            
    #         returnBuilder.update({"jobId": jobId})
            
    #         redis_conn = django_rq.get_connection()
    #         job = django_rq.jobs.Job(jobId, redis_conn)
            
    #        # print(job.get_position())
    #    #      quePosition = django_rq.get_queue().job_ids.index(jobId)
    #         if(job.is_queued):
    #             returnBuilder.update({"queuePosition": django_rq.get_queue().get_job_position(job)})
    #         returnBuilder.update({})
    #     return(Response(returnBuilder))