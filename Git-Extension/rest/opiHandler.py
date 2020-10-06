import requests as requestsLib
import xmltodict
class OpiHandler:
    def opiUrlRequest(urlToOntology: str, classMetrics=False) -> dict:
        OntoMetricsEndPoint = "http://opi.informatik.uni-rostock.de/api?url="
        resp = requestsLib.get(OntoMetricsEndPoint + urlToOntology, headers = {"save": "false", "classMetrics": str(True)})
        if(resp.status_code != 200):
            print(resp.status_code)
        else:
            xmlText = resp.text.replace("\n", "")
            xmlText = xmlText.replace("@", "")
            xmlDict = xmltodict.parse(xmlText,)
            return xmlDict
    
    def opiOntologyRequest(ontologyString: str, classMetrics=False) -> dict:
        OntoMetricsEndPoint = "http://opi.informatik.uni-rostock.de/api"#
        resp = requestsLib.post(url=OntoMetricsEndPoint, data=ontologyString, headers = {"save": "false", "classMetrics": str(classMetrics)})
        if (resp.status_code != 200):
            print(resp.status_code)
        else:
            xmlText = resp.text.replace("\n", "")
            xmlDict = xmltodict.parse(xmlText,)

            # Restructure ClassMetrics for a more flat hierachy
            if classMetrics:
                tmpClassList =[]
                for element in xmlDict["OntologyMetrics"]["BaseMetrics"]["ClassMetrics"]["Class"]:
                    element["Classiri"] = element["@iri"]
                    element.pop("@iri")
                    tmpClassList.append(element)
            xmlDict["OntologyMetrics"]["BaseMetrics"].pop("ClassMetrics")
            xmlDict["OntologyMetrics"]["BaseMetrics"].update({"Classmetrics": tmpClassList})
            
            
            return xmlDict
    