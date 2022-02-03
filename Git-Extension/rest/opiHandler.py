import requests as requestsLib
from django.conf import settings
import xmltodict


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
                               headers={"save": "false", "classMetrics": str(classMetrics)})
        if(resp.status_code != 200):
            print(resp.status_code)
        else:
            xmlText = resp.text.replace("\n", "")
            xmlText = xmlText.replace("@", "")
            xmlDict = xmltodict.parse(xmlText,)
            return xmlDict

    def opiOntologyRequest(self, ontologyString: str, classMetrics=True, reasoner=False) -> dict:
        """Make a URL to an OPI server and return a dict with the Ontology-Data .

        Args:
            ontologyString (str): Ontology-Data
            classMetrics (bool, optional): indicates whether the service shall compute individual Metrics for every class (Computational expensive). Defaults to False.
            reasoner (bool, optional): The ontology calculation enginge can run a reasoner (HermiT) prior to the metric calculation. Takes more time.

        Returns:
            dict: Ontology-Metrics
        """

        resp = requestsLib.post(url=self.OntoMetricsEndPoint, data=ontologyString, headers={
                                "save": "false", "reasoner": str(reasoner), "classMetrics": str(classMetrics)})
        if (resp.status_code != 200):
            raise IOError(resp.text)
        else:
            xmlText = resp.text.replace("\n", "")
            xmlDict = xmltodict.parse(xmlText,)

            # Restructure ClassMetrics for a more flat hierachy
            if classMetrics:
                tmpClassList = []
                if (xmlDict["OntologyMetrics"]["BaseMetrics"]["ClassMetrics"]):
                    for element in xmlDict["OntologyMetrics"]["BaseMetrics"]["ClassMetrics"]["Class"]:
                        element["Classiri"] = element["@iri"]
                        element.pop("@iri")
                        tmpClassList.append(element)
                    xmlDict["OntologyMetrics"]["BaseMetrics"].pop(
                        "ClassMetrics")
                    xmlDict["OntologyMetrics"]["BaseMetrics"].update(
                        {"Classmetrics": tmpClassList})
            return xmlDict
