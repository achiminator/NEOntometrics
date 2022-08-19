import rdflib

rdf = rdflib.Graph(store="Memory")
rdf.parse(
    location="rest/metricOntology/OntologyMetrics.owl", format="application/rdf+xml")

with open("rest/metricOntology/metricExplorerQuery.sparql", "r") as f:
    query = f.read()
qres = rdf.query(query)
with open("rest/metricOntology/metricQuery.sparql", "r") as f:
    query = f.read()
qres = rdf.query(query)