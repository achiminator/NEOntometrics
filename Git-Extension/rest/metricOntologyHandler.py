import rdflib, os, re

class OntologyHandler:
    def __init__(self):
        self.rdf = rdflib.Graph(store="OxMemory")
        self.rdf.parse(location="OntologyMetrics.owl", format="application/rdf+xml")
    @staticmethod
    def resURI2str(uriResult: str) -> str:
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
# oh = ontologyHandler()
# print(oh.getImplementedMetrics())