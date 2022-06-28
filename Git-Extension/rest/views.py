from django.http import HttpResponse, JsonResponse
import rest.metricOntologyHandler
from rest.CalculationManager import CalculationManager
from rest_framework.views import APIView
from rest_framework.response import Response
import rest.queueInformation

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


class MetricExplorer(APIView):
    # Calculating the Data for the Metric Explorer Frontent from the ontology is a quite extensive task, which
    # is not required for the worker applications (those who run and distribute the metric calculations).
    # The environment variable "isWorker" is set by the docker-compose file.

    # if not(bool(os.environ.get("isWorker", False))) and bool(os.environ.get("inDocker", False)):
    def get(self, request, format=None):
        explorer = rest.metricOntologyHandler.ontologyhandler.getMetricExplorer()
        return(Response(explorer))

class DownloadOntologyFile(APIView):
    """Downloads one specific Ontology File.
    """
    def get(self, request, pk, format=None):
        file = CalculationManager().downloadSpecificOntologyFile(pk)
        for ontologyName, content in file.items():
            response = HttpResponse(content, content_type='text/plain')
            response['Content-Disposition'] = f'attachment; filename={ontologyName}'
            # response.write(file.values[0])
            return response
