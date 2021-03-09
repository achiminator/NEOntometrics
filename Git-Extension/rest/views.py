
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
        raise argparse.ArgumentTypeError('Boolean value expected.')