from copy import copy, deepcopy
import os
import rdflib
import re
import treelib
import pickle

class OntologyHandler:
    def __init__(self):
        self.getMetricExplorer()

    @staticmethod
    def resURI2str(uriResult: str) -> str:
        """Extracts the clss names from the URI-Representation of the Sparql-Results

        Args:
            uriResult (str): The result item from the sparql query (e.g.: "http://uni-rostock.de/NEOntometrics#CohesionMetric")

        Returns:
            str: The class name without the URI-representatin (e.g.: "CohesionMetric")
        """
        return re.sub("((.*)#)", "", uriResult).replace("_", " ")

    def getElementalMetricDescriptions(self, rdf) -> rdflib.query.Result:
        qres = rdf.query("""
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

    def getImplementedMetrics(self) -> list:
        """Extracts the list of metrics that are already implemented according to the
        statements in the ontology. Useful for consistency checking whether these elements
        are actually implemented or not.

        Returns:
            list: List of implemented metrics, according to ontology.
        """
        rdf = rdflib.Graph(store="OxMemory")
        rdf.parse(
            location="rest/metricOntology/OntologyMetrics.owl", format="application/rdf+xml")
        qres = self.getElementalMetricDescriptions(rdf)
        ontologyImplementedMetrics = []
        for row in qres:
            ontologyImplementedMetrics.append(
                self.resURI2str(row["implementedAs"]))
        return ontologyImplementedMetrics


    def getMetricDict(self) -> list:
        """Build a list of available Metrics (in the form of a dict) to enable the future calculation of these metrics.

        Returns:
            list: Metric Dictionary. 
        """

        # As the method is pretty computational expensive, first check whether the data is already available.
        if hasattr(self, "metricDict"):
            return(self.metricDict)
        else:
            raise Exception("Fatal Error: MetricDict Accessed before calculated")
    def calculateMetricDict(self, rdf)->list:
        with open("rest/metricOntology/metricQuery.sparql", "r") as f:
            query = f.read()
        qres = rdf.query(query)
        metricDict = []
        calculationDict = {}
        previousitem = None
        for line in qres:
            if("Orme" in line[0]):
                print("pass")
            if previousitem == None and not calculationDict.get("directlyUsesMetric"):
                previousitem = line
            elif previousitem["metric"] != line["metric"]:
                print(self.__buildLambdaFunctionFromOntology(calculationDict))
                metricDict.append(self.buildMetricDict(calculationDict, previousitem))
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
                        calculationDict[self.resURI2str(line["property"])].update({
                            self.resURI2str(line["property2"]): [self.resURI2str(line["value2"])]
                        })
                    else:
                        calculationDict[self.resURI2str(line["property"])].get(
                            self.resURI2str(line["property2"])).append(self.resURI2str(line["value2"]))

                else:
                    if calculationDict.get(self.resURI2str(line["property"])) == None:
                        calculationDict.update({self.resURI2str(line["property"]): [
                                               self.resURI2str(line["value"])]})
                    else:
                        calculationDict.get(self.resURI2str(line["property"])).append(
                            self.resURI2str(line["value"]))

            previousitem = line

        metricDict.append(self.buildMetricDict(calculationDict=calculationDict, previousitem=previousitem))

        self.metricDict = metricDict
        return metricDict

    def buildMetricDict(self, calculationDict, previousitem):
        return {
                    "framework": self.resURI2str(previousitem["framework"]),
                    "metricCategory": self.resURI2str(previousitem["metricCategory"]),
                    "metric": self.resURI2str(previousitem["metric"]),
                    "metricDescription": str(previousitem["description"]),
                    "metricDefinition": str(previousitem["definition"]),
                    "metricInterpretation": str(previousitem["interpretation"]),
                    "metricCalculation": self.__buildLambdaFunctionFromOntology(calculationDict)
                }

    def __buildLambdaFunctionFromOntology(self, calculationDict: dict) -> str:
        """Takes the preliminary Metric calculation that is calculated in the calling method getMetricDict and 
        builds a ready to use function for the actual calculation of these metrics based on the stored metric data

        Args:
            calculationDict (dict): Preliminary structure "calculationDict" of the getMetricDict Method

        Raises:
            IndexError: For some elements, the ontology shall not contain more than one element (e.g., directlyUsesMetric). If 
            nevertheless, such problem occures, this method raises an error.

        Returns:
            str: A ready to use calculation function
        """
        calculationParts = []
        calculationConnectedBy = ""
        returnString = ""

        if calculationDict.get("numerator") and calculationDict.get("divisor"):
            calculationConnectedBy = "/"
            if isinstance(calculationDict["numerator"], dict):
                calculationParts.insert(
                    0, self.__buildLambdaFunctionFromOntology(calculationDict["numerator"]))
            else:
                # This check here validates that the field numerator is really just used once (otherwise, the error below is thrown.)
                # While complex decompositions of these values are still possible (e.g., the sum of two elements in the numerator),
                # this check here is checking something different (more than one declaration of a field that can just used once in the metric ontology).
                if len(calculationDict["numerator"]) > 1:
                    raise IndexError(
                        "The numerator of a Metric contains more than one element (More than expected) and is not declared so")
                else:
                    calculationParts.insert(0, calculationDict["numerator"][0])
            if isinstance(calculationDict["divisor"], dict):
                calculationParts.insert(
                    1, self.__buildLambdaFunctionFromOntology(calculationDict["divisor"]))
            else:
                if len(calculationDict["divisor"]) > 1:
                    raise IndexError(
                        "The divisor of a Metric contains more than one element (More than expected) and is not declared so")
                else:
                    calculationParts.insert(1, calculationDict["divisor"][0])

        elif calculationDict.get("minuend") and calculationDict.get("subtrahend"):
            calculationConnectedBy = "-"
            if isinstance(calculationDict["minuend"], dict):
                calculationParts.insert(
                    0,  self.__buildLambdaFunctionFromOntology(calculationDict["minuend"]))
            else:
                if len(calculationDict["minuend"]) > 1:
                    raise IndexError(
                        "The minuend of a Metric contains more than one element (More than expected) and is not declared so")
                else:
                    calculationParts.insert(0, calculationDict["minuend"][0])
            if isinstance(calculationDict["subtrahend"], dict):
                calculationParts.insert(1, self.__buildLambdaFunctionFromOntology(
                    calculationDict["subtrahend"]))
            else:
                if len(calculationDict["subtrahend"]) > 1:
                    raise IndexError(
                        "The subtrahend of a Metric contains more than one element (More than expected) and is not declared so")
                else:
                    calculationParts.insert(
                        1, calculationDict["subtrahend"][0])

        elif calculationDict.get("sum"):
            calculationConnectedBy = "+"
            calculationParts = calculationDict["sum"]

        elif calculationDict.get("multiplication"):
            calculationConnectedBy = "*"
            calculationParts = calculationDict["multiplication"]

        elif calculationDict.get("directlyUsesMetric"):
            if len(calculationDict["directlyUsesMetric"]) > 1:
                raise IndexError(
                    "The direct conecction to a Metric in the Ontology contains more than one element (More than expected)")
            calculationParts.append(
                calculationDict.get("directlyUsesMetric")[0])

        if(calculationConnectedBy in ["*", "+", ""]):
            for calculationPart in calculationParts:
                returnString += " " + calculationPart + " " + calculationConnectedBy
            returnString = returnString[1:-1]
        elif(calculationConnectedBy in ["/", "-"]):
            returnString = "(" + str(calculationParts[0]) + ") " + str(
                calculationConnectedBy) + " (" + str(calculationParts[1]) + ")"
        return returnString

    def getMetricExplorer(self) -> list:
        """Fetches all data to display the metric explorer in the frontend.

        Returns:
            list: JSON-Tree with the relevant information.
        """
        rdf = rdflib.Graph(store="OxMemory")
        rdf.parse(
            location="rest/metricOntology/OntologyMetrics.owl", format="application/rdf+xml")

        # As the method is pretty computational expensive, first check whether the data is already available.
        if (not(hasattr(self, "metricDict"))):
            self.metricDict = self.calculateMetricDict(rdf)
        if hasattr(self, "tree"):
            return self.tree.to_dict(with_data=True)
        with open("rest/metricOntology/metricExplorerQuery.sparql", "r") as f:
            query = f.read()
        qres = rdf.query(query)
        tree = treelib.Tree()

        metricExplorerData = []
        tree.create_node(identifier="thing")
        for metricItem in qres:
            implemented = False
            if(tree.get_node(self.resURI2str(metricItem["itemName"])) == None):
                nodeData = {
                    "itemName": self.resURI2str(metricItem["itemName"]),
                    "description": metricItem["description"],
                    "definition": metricItem["definition"],
                    "seeAlso": metricItem["seeAlso"],
                    "interpretation": metricItem["interpretation"],
                    "implementationName": self.resURI2str(metricItem["implementationName"]) if metricItem["implementationName"] != None else "",
                }
                for metricDictEntry in self.metricDict:
                    if(self.resURI2str(metricItem["itemName"]) == metricDictEntry["metric"]):
                        implemented = True
                        calculation = metricDictEntry["metricCalculation"]
                        break
                if(implemented == True):
                    nodeData.update({
                        "calculation": calculation
                    })
                tree.create_node(tag="item", identifier=self.resURI2str(
                    metricItem["itemName"]), parent="thing", data=nodeData)
        tree.show()
        for metricItem in qres:
            if(isinstance(metricItem["superClass"], rdflib.URIRef) and tree.contains(self.resURI2str(metricItem["superClass"]))):
                tree.move_node(self.resURI2str(
                    metricItem["itemName"]), self.resURI2str(metricItem["superClass"]))
        self.tree = tree

        return tree.to_dict(with_data=True)

# Creating the ontologyHandler object queries the ontology itself with all the SPARQL-Queries. That can be tremendously slow. So for development, it is more 
# convinient to store the ontologyhandler in a pickle file to 
if os.path.exists("rest/metricOntology/pickleDump.pickle") and bool(os.environ.get("inDocker", False)) == False:
    with open("rest/metricOntology/pickleDump.pickle", "rb") as file:
        ontologyhandler = pickle.load(file)
else:
    ontologyhandler = OntologyHandler()
    with open("rest/metricOntology/pickleDump.pickle", "wb") as file:
        pickle.dump(ontologyhandler, file)
