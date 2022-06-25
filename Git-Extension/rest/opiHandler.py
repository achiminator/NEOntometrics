from json import JSONDecoder
import json
import requests as requestsLib
from django.conf import settings
import xmltodict
import sys


class OpiHandler:
    """Class for querying the Ontology Programming Interface (OPI)"""

    OntoMetricsEndPoint = "http://" + settings.OPI + "/api"

    def opiUrlRequest(self, urlToOntology: str, classMetrics=False) -> dict:
        """Make a URL to an OPI server and return a dict with the Ontology-Data .

        Args:
            urlToOntology (str): http-URL to OntologyFile
            classMetrics (bool, optional): indicates whether the service shall compute individual Metrics for every class (Computational expensive). Defaults to False.

        Returns:
            dict: Ontology-Metrics
        """

        resp = requestsLib.get(self.OntoMetricsEndPoint + urlToOntology,
                               headers={"classMetrics": str(classMetrics)})
        if(resp.status_code != 200):
            print(resp.status_code)
        else:
            return resp.json()  

    def opiOntologyRequest(self, ontologyString: str, ontologySize: int, classMetrics=True, reasoner=False) -> dict:
        """Make a URL to an OPI server and return a dict with the Ontology-Data .

        Args:
            ontologyString (str): Ontology-Data
            classMetrics (bool, optional): indicates whether the service shall compute individual Metrics for every class (Computational expensive). Defaults to False.
            reasoner (bool, optional): The ontology calculation enginge can run a reasoner (HermiT) prior to the metric calculation. Takes more time.

        Returns:
            dict: Ontology-Metrics
        """
        metrics = {}
        error = ""
        # The http-Request returns a "str", object, but the Git-Object returns
        # a byte. However, with the implicit str encoding, some errors can occur for some 
        # ontologies. This explicit conversion into utf-8 should prevent these problems.
        if(type(ontologyString) == str):
            ontologyString = ontologyString.encode("utf-8")
        # if (ontologySize > settings.CLASSMETRICSLIMIT and classMetrics and settings.CLASSMETRICSLIMIT > 0):
        #     classMetrics = False
        #     error = "Ontology Exceeds " + \
        #         str(settings.CLASSMETRICSLIMIT) + \
        #         "b. ClassMetrics deactivated"
        #     self.logger.warning(
        #         + "ontoloy to large: " + ontologySize + " - ClassMetrics Deativated")
        if(ontologySize > settings.ONTOLOGYLIMIT and settings.ONTOLOGYLIMIT > 0):
            error += "Ontology Exceeds " + \
                str(settings.ONTOLOGYLIMIT) + \
                "b. Analysis deactivated. "
            self.logger.error(error)
        elif(ontologySize > settings.REASONINGLIMIT and settings.REASONINGLIMIT > 0 and reasoner):
            reasoner = False
            error += "Ontology Exceeds " + \
            str(settings.REASONINGLIMIT) + \
            "b. Reasoning deactivated. "
        else:
            counter = 0
            metricResponse = None
            try:
                metricResponse = requestsLib.post(url=self.OntoMetricsEndPoint, data=ontologyString,  timeout=settings.OPITIMEOUT, headers={
                    "save": "false", "reasoner": str(reasoner), "classMetrics": str(classMetrics)})
            except requestsLib.exceptions.Timeout:
                # At first, retry and check if the error happens again
                try:
                    metricResponse = requestsLib.post(url=self.OntoMetricsEndPoint, data=ontologyString,  timeout=settings.OPITIMEOUT, headers={
                            "save": "false", "reasoner": str(reasoner), "classMetrics": str(classMetrics)})
                except:
                    error += "Request to Calculation Service timed out. It took longer than " + str(settings.OPITIMEOUT) + "seconds"
            except requestsLib.exceptions.ConnectionError:
                error += "Connection Error. Cannot reach OPI-Service"
            if(metricResponse):
                if(metricResponse.status_code != 200):
                    error += metricResponse.text
                else:
                    metrics = metricResponse.json()

                # Restructure ClassMetrics for a more flat hierachy
                if classMetrics:
                    tmpClassList = []
                    if (metrics["OntologyMetrics"]["BaseMetrics"]["ClassMetrics"]):
                        for element in metrics["OntologyMetrics"]["BaseMetrics"]["ClassMetrics"]["Class"]:
                            element["Classiri"] = element["@iri"]
                            element.pop("@iri")
                            tmpClassList.append(element)
                        metrics["OntologyMetrics"]["BaseMetrics"].pop(
                            "ClassMetrics")
                        metrics["OntologyMetrics"]["BaseMetrics"].update(
                            {"Classmetrics": tmpClassList})
                
            # There are some metrics, that are not returned by the OPI-application.
        metrics.update({"ReadingError": error,
                        "Size": ontologySize})
        return metrics
