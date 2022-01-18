import rdflib, os, re
#from rest.models import Source, Metrics

class OntologyHandler:
    def __init__(self):
        self.rdf = rdflib.Graph(store="OxMemory")
        self.rdf.parse(location="rest/metricOntology/OntologyMetrics.owl", format="application/rdf+xml")
    @staticmethod
    def resURI2str(uriResult: str) -> str:
        """Extracts the clss names from the URI-Representation of the Sparql-Results

        Args:
            uriResult (str): The result item from the sparql query (e.g.: "http://uni-rostock.de/NEOntometrics#CohesionMetric")

        Returns:
            str: The class name without the URI-representatin (e.g.: "CohesionMetric")
        """
        return  re.sub("((.*)#)", "", uriResult).replace("_", " ")
    def getElementalMetricDescriptions(self)-> rdflib.query.Result:
        qres = self.rdf.query("""
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
        PREFIX metric: <http://neontometrics.informatik.informatik.uni-rostock.de#>
        SELECT ?metric ?description ?definition ?implementedAs 
        WHERE {
            ?metric rdfs:subClassOf+ metric:Elemental_Metrics.
            ?metric rdfs:subClassOf+ _:a .
        _:a owl:hasValue ?implementedAs .
            OPTIONAL { ?metric metric:MetricDescription ?description } .
            OPTIONAL {	?metric metric:MetricDefinition ?definition }
        . FILTER (NOT EXISTS{?sub rdfs:subClassOf ?metric})
        }
        """)
        return qres
        for row in qres:
            print(self.resURI2str(row[0]))
            print(str(row[1]))
        
    def getImplementedMetrics(self)->list:
        """Extracts the list of metrics that are already implemented according to the
        statements in the ontology. Useful for consistency checking whether these elements
        are actually implemented or not.

        Returns:
            list: List of implemented metrics, according to ontology.
        """
        qres = self.getElementalMetricDescriptions()
        ontologyImplementedMetrics = []
        for row in qres:
            ontologyImplementedMetrics.append(self.resURI2str(row["implementedAs"]))
        return ontologyImplementedMetrics

    def getMetricDict(self):
        with open("rest/metricOntology/metricQuery.sparql", "r") as f:
            query = f.read()
        qres = self.rdf.query(query)
        metricDict = []
        calculationDict = {}
        previousitem = None
        for line in qres:
            if(str(line["metric"]) == "http://neontometrics.informatik.informatik.uni-rostock.de#OQuaRE_PROnto"):
                print("BAAAAAMMM!")
            if previousitem == None and not calculationDict.get("directlyUsesMetric"):
                previousitem = line
            elif previousitem["metric"] != line["metric"]: 
                print(self.buildLambdaFunctionFromOntology(calculationDict))
                metricDict.append({
                    "framework": self.resURI2str(previousitem["framework"]),
                    "metricCategory": self.resURI2str(previousitem["metricCategory"]),
                    "metric": self.resURI2str(previousitem["metric"]),
                    "metricDescription": str(previousitem["description"]),
                    "metricDefinition": str(previousitem["definition"]),
                    "metricInterpretation": str(previousitem["interpretation"]),
                    "metricCalculation": self.buildLambdaFunctionFromOntology(calculationDict)
                })
                calculationDict = {}
            if(line["property"] != None and line["value"] != None):
                #print (line["metric"] + ", " + line["property"] + ", " + line["value"] + "\\n" )
                if(str(line["value"]) == "seeProperty2"):
                    if calculationDict.get(self.resURI2str(line["property"])) == None:
                            calculationDict.update({
                                self.resURI2str(line["property"]):  {
                                    self.resURI2str(line["property2"]): [self.resURI2str(line["value2"])]
                                }
                            })
                    elif calculationDict[self.resURI2str(line["property"])].get(self.resURI2str(line["property2"])) == None:
                        calculationDict.update({
                            self.resURI2str(line["property"]):  {
                                self.resURI2str(line["property2"]): [self.resURI2str(line["value2"])]
                            }
                        })
                    else:
                        calculationDict[self.resURI2str(line["property"])].get(self.resURI2str(line["property2"])).append(self.resURI2str(line["value2"])) 

                else:
                    if calculationDict.get(self.resURI2str(line["property"])) == None:
                        calculationDict.update({self.resURI2str(line["property"]): [self.resURI2str(line["value"])]})
                    else:
                        calculationDict.get(self.resURI2str(line["property"])).append(self.resURI2str(line["value"]))

            previousitem = line
        metricDict.append({
            "framework": previousitem["framework"],
            "metricCategory": previousitem["metricCategory"],
            "metric": previousitem["metric"],
            "metricDescription": previousitem["description"],
            "metricDefinition": previousitem["definition"],
            "metricInterpretation": previousitem["interpretation"],
            "metricCalculation": calculationDict
        })
    def buildLambdaFunctionFromOntology(self, calculationDict: dict)->str:
        calculationParts = []
        calculationConnectedBy = ""
        returnString = ""       
        
        
        if calculationDict.get("numerator") and calculationDict.get("divisor"):
            calculationConnectedBy = "/"
            if isinstance(calculationDict["numerator"], dict):
                calculationParts.insert(0, self.buildLambdaFunctionFromOntology(calculationDict["numerator"]))
            else:
                # This check here validates that the field numerator is really just used once (otherwise, the error below is thrown.)
                # While complex decompositions of these values are still possible (e.g., the sum of two elements in the numerator),
                # this check here is checking something different (more than one declaration of a field that can just used once in the metric ontology).
                if len(calculationDict["numerator"]) >1:
                    raise IndexError("The numerator of a Metric contains more than one element (More than expected) and is not declared so")
                else:
                    calculationParts.insert(0, calculationDict["numerator"][0])
            if isinstance(calculationDict["divisor"], dict):
                calculationParts.insert(1, self.buildLambdaFunctionFromOntology(calculationDict["divisor"]))
            else:
                if len(calculationDict["divisor"])>1:
                    raise IndexError("The divisor of a Metric contains more than one element (More than expected) and is not declared so")
                else:
                    calculationParts.insert(1, calculationDict["divisor"][0])
        
        elif calculationDict.get("minuend") and calculationDict.get("subtrahend"):
            calculationConnectedBy = "-"
            if isinstance(calculationDict["minuend"], dict):
                calculationParts.insert(0,  self.buildLambdaFunctionFromOntology(calculationDict["minuend"]))
            else:
                if len(calculationDict["minuend"]) >1:
                    raise IndexError("The minuend of a Metric contains more than one element (More than expected) and is not declared so")
                else:
                    calculationParts.insert(0, calculationDict["minuend"][0])
            if isinstance(calculationDict["subtrahend"], dict):
                calculationParts.insert(1, self.buildLambdaFunctionFromOntology(calculationDict["subtrahend"]))
            else:
                if len(calculationDict["subtrahend"]) >1:
                    raise IndexError("The subtrahend of a Metric contains more than one element (More than expected) and is not declared so")
                else:
                    calculationParts.insert(1, calculationDict["subtrahend"][0])
        
        elif calculationDict.get("sum"):
            calculationConnectedBy = "+"
            calculationParts = calculationDict["sum"]
        
        elif calculationDict.get("multiplication"):
            calculationConnectedBy = "*"
            calculationParts = calculationDict["multiplication"]
        
        elif calculationDict.get("directlyUsesMetric"):
            if len(calculationDict["directlyUsesMetric"]) >1:
                    raise IndexError("The direct conecction to a Metric in the Ontology contains more than one element (More than expected)")
            calculationParts.append(calculationDict.get("directlyUsesMetric")[0])

        if(calculationConnectedBy in ["*", "+", ""]):
            for calculationPart in calculationParts:
                returnString +=" "+ calculationPart + " " + calculationConnectedBy
        elif(calculationConnectedBy in ["/", "-"]):
            returnString = "(" + str(calculationParts[0]) + ") "+ str(calculationConnectedBy) + " (" + str(calculationParts[1]) + ")"
        return returnString
                
    #     if(calculation == "")
oh = OntologyHandler()
# print(oh.getImplementedMetrics())
oh.getMetricDict()