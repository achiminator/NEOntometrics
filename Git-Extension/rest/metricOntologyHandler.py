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
            str: The class name witho ut the URI-representatin (e.g.: "CohesionMetric")
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
        rdf = rdflib.Graph(store="Oxigraph")
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


        def buildCalculationDict(calculationDict: dict, level:int, line)->dict:
            """Recursive function for extracting the calculation objects out of the SPARQL-request and transforming it into a dict

            Args:
                calculationDict (dict): The current calculation dict to extent
                level (int): The recursion level
                line (_type_): the SPARQL-result line

            Returns:
                dict: Build nested dictionary containing the function.
            """
            
            if(line["property"+ str(level)] != None and line["value"+ str(level)] != None):
                if(str(line["value" + str(level)]) == "seeProperty"+ str(level + 1)):
                    s = self.resURI2str(line["property"+ str(level)])
                    if (s in calculationDict):
                        calculationDict.update({s: buildCalculationDict(calculationDict[s], level+1, line)})
                    else:                
                        calculationDict.update({s: buildCalculationDict({}, level+1, line)})
                else:
                    if calculationDict.get(self.resURI2str(line["property" + str(level)])) == None:
                        calculationDict.update({self.resURI2str(line["property" + str(level)]): [
                                               self.resURI2str(line["value"+ str(level)])]})
                    else:
                        calculationDict.get(self.resURI2str(line["property" + str(level)])).append(
                            self.resURI2str(line["value"+str(level)]))
            return calculationDict
            

        with open("rest/metricOntology/metricQuery.sparql", "r") as f:
            query = f.read()
        qres = rdf.query(query)
        metricDict = []
        calculationDict = {}
        previousitem = None
        for line in qres:
            if("RFCOnto" in line[0]):
                print("pass")
            if previousitem == None and not calculationDict.get("directlyUsesMetric"):
                previousitem = line
            elif previousitem["metric"] != line["metric"]:
                print(self.__buildLambdaFunctionFromOntology(calculationDict))
                metricDict.append(self.buildMetricDict(calculationDict, previousitem))
                calculationDict = {}
            calculationDict.update(buildCalculationDict(calculationDict=calculationDict, level= 1, line=line))

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

        def splitCalculationDict(calculationDict:dict)->list:
            returnList = []
            input = deepcopy(calculationDict)
            if "divisor" in input and "numerator" in input:
                returnList.append({"divisor": input["divisor"], "numerator": input["numerator"]})
                input.pop("divisor")
                input.pop("numerator")
            if("minuend" in input and "subtrahend" in input):
                returnList.append({"minuend": input["minuend"], "subtrahend": input["subtrahend"]})
                input.pop("minuend")
                input.pop("subtrahend")
            
            returnList.append(input)
            return returnList

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
            if(type(calculationDict["multiplication"]) == dict):
                calculationParts = []
                for element in splitCalculationDict(calculationDict["multiplication"]):

                    calculationParts.append(self.__buildLambdaFunctionFromOntology(element))
            else:
                calculationParts = calculationDict["multiplication"]

        elif calculationDict.get("directlyUsesMetric"):
            if len(calculationDict["directlyUsesMetric"]) > 1:
                raise IndexError(
                    "The direct conecction to a Metric in the Ontology contains more than one element (More than expected)")
            calculationParts.append(
                calculationDict.get("directlyUsesMetric")[0])

        if(calculationConnectedBy in ["*", "+", ""]):
            for calculationPart in calculationParts:
                if(" " in calculationPart): # If there is a whitespace, then there is more than one metric involved and we need brackets
                    returnString += " (" + calculationPart + ") " + calculationConnectedBy    
                else:
                    returnString += " " + calculationPart + " " + calculationConnectedBy
            returnString = returnString[1:-1]
        elif(calculationConnectedBy in ["/", "-"]):
            if " " in calculationParts[0]:
                calculationParts[0] = "(" + str(calculationParts[0]) + ")"
            if " " in calculationParts[1]:
                calculationParts[1] = "(" + str(calculationParts[1]) + ")"
            returnString = str(calculationParts[0]) + " " + str(
                calculationConnectedBy) + " " + str(calculationParts[1]) 
        return returnString

    def getMetricExplorer(self) -> list:
        """Fetches all data to display the metric explorer in the frontend.

        Returns:
            list: JSON-Tree with the relevant information.
        """
        rdf = rdflib.Graph(store="Memory")
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
