<<<<<<< HEAD
=======
<<<<<<< HEAD

from rest.git import GitHandler
from rest.opiHandler import OpiHandler
from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.request import Request
from rest_framework.exceptions import APIException, ParseError
import django_rq

# Create your views here.

class CalculateMetric(APIView):
    def get(self, request, format=None):
        opi = OpiHandler
        url = request.GET.get("url")
        xmlDict = opi.opiUrlRequest(url)
        return(Response(xmlDict))


class CalculateGitMetric(APIView):
    def get(self, request: Request, format=None):
        targetLocation = ""
        try:
            if "classMetrics" in request.query_params:
                classMetrics = str2bool(request.query_params["classMetrics"])
            else:
                classMetrics = False
            if "branch" in request.query_params:
                branch = request.query_params["branch"]
            else:
                branch = "master"
            repository = request.query_params["repository"]
            if "target" in request.query_params:
                targetLocation = request.query_params["target"]
        except KeyError as identifier:
            print(identifier)
            raise ParseError("Wrong Input parameter! Check Documentation")
        
        
        gitHandler = GitHandler()
        if targetLocation != "":
            metrics = django_rq.enqueue(gitHandler.getObject, repositoryUrl=repository, objectLocation=targetLocation, branch=branch, classMetrics=classMetrics)
            #metrics = gitHandler.getObject(repositoryUrl=repository, objectLocation=targetLocation, branch=branch, classMetrics=classMetrics)
        else:
            metrics = gitHandler.getObjects(repositoryUrl=repository, branch=branch, classMetrics=classMetrics)
            
        return(Response(str(metrics)))

def str2bool(v: str) -> bool:
    """Converts a String to a bool value

    Args:
        v (str): Input Value

    Raises:
        argparse.ArgumentTypeError: No Boolean Value detected

    Returns:
        bool: Boolean value
    """
    if isinstance(v, bool):
        return v
    if v.lower() in ('yes', 'true', 't', 'y', '1'):
        return True
    elif v.lower() in ('no', 'false', 'f', 'n', '0'):
        return False
    else:
=======

>>>>>>> 7bbae86413b05108737c10a2e2ad02c78a49701d
from rest.git import GitHandler
from rest.opiHandler import OpiHandler
from django.shortcuts import render
from rest_framework.views import APIView
<<<<<<< HEAD
from rest_framework.response import Response
from rest_framework.request import Request
from rest_framework.exceptions import APIException, ParseError
import django_rq, rq, redis
from rest.GitHelper import GitHelper, GitUrlParser
=======
#from rest_framework.renderers import 
from rest_framework.response import Response
from rest_framework.request import Request
from rest_framework.exceptions import APIException, ParseError

>>>>>>> 7bbae86413b05108737c10a2e2ad02c78a49701d
# Create your views here.

class CalculateMetric(APIView):
    def get(self, request, format=None):
        opi = OpiHandler
        url = request.GET.get("url")
        xmlDict = opi.opiUrlRequest(url)
        return(Response(xmlDict))


class CalculateGitMetric(APIView):
<<<<<<< HEAD
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
=======
    def get(self, request: Request, format=None):
        targetLocation = ""
        try:
            if "classMetrics" in request.query_params:
                classMetrics = str2bool(request.query_params["classMetrics"])
>>>>>>> 7bbae86413b05108737c10a2e2ad02c78a49701d
            else:
                classMetrics = False
            if "branch" in request.query_params:
                branch = request.query_params["branch"]
            else:
                branch = "master"
<<<<<<< HEAD
=======
            repository = request.query_params["repository"]
            if "target" in request.query_params:
                targetLocation = request.query_params["target"]
>>>>>>> 7bbae86413b05108737c10a2e2ad02c78a49701d
        except KeyError as identifier:
            print(identifier)
            raise ParseError("Wrong Input parameter! Check Documentation")
        
        
<<<<<<< HEAD
        
        gitHandler = GitHandler()
        if GitUrlParser.file != "":
            metrics = django_rq.enqueue(gitHandler.getObject, repositoryUrl=url.repository, objectLocation=url.file, branch=url.branch, classMetrics=classMetrics, job_id=GitHelper.serializeJobId(url.url))       
        else:
            metrics = django_rq.enqueue(gitHandler.getObjects, repositoryUrl=url.repository, branch=branch, classMetrics=classMetrics, job_id=GitHelper.serializeJobId(url.url))            
        return(Response(GitHelper.deserializeJobId(str(metrics.id))))

class CalculatedMetrics(APIView):
    
    def get(self, request: Request, format=None):
        returnBuilder = {}
        if("job" in request.query_params):
            
            jobId = request.query_params["job"]
            returnBuilder.update({"jobId": jobId})
            
            redis_conn = django_rq.get_connection()
            job = django_rq.jobs.Job(jobId, redis_conn)
            
           # print(job.get_position())
       #      quePosition = django_rq.get_queue().job_ids.index(jobId)
            if(job.is_queued):
                returnBuilder.update({"queuePosition": django_rq.get_queue().get_job_position(job)})
            returnBuilder.update({})
        return(Response(returnBuilder))

=======
        gitHandler = GitHandler()
        if targetLocation != "":
            metrics = gitHandler.getObject(repositoryUrl=repository, objectLocation=targetLocation, branch=branch, classMetrics=classMetrics)
        else:
            metrics = gitHandler.getObjects(repositoryUrl=repository, branch=branch, classMetrics=classMetrics)
            
        return(Response(metrics))

def str2bool(v: str) -> bool:
    """Converts a String to a bool value

    Args:
        v (str): Input Value

    Raises:
        argparse.ArgumentTypeError: No Boolean Value detected

    Returns:
        bool: Boolean value
    """
    if isinstance(v, bool):
        return v
    if v.lower() in ('yes', 'true', 't', 'y', '1'):
        return True
    elif v.lower() in ('no', 'false', 'f', 'n', '0'):
        return False
    else:
>>>>>>> 62d05d48126ff5b77054d9241ccbd26e25686fef
        raise argparse.ArgumentTypeError('Boolean value expected.')
>>>>>>> 7bbae86413b05108737c10a2e2ad02c78a49701d
