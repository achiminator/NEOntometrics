# Ontology Programming Interface *(opi*)
The ontology programming interface is the metric calculation engine for NEOntometrics. It receives an ontology via POST-request and returns the implemented elemental metrics, represented as individuals in the [metric ontology](http://ontology.neontometrics.com). The software is a predecessor of:

*Reiz, Achim; Dibowski, Henrik; Sandkuhl, Kurt; Lantow, Birger (2020 - 2020): Ontology Metrics as a Service (OMaaS). In Joaquim Filipe, David Aveiro, Jan L.G. Dietz (Eds.): Proceedings of the 12th International Joint Conference on Knowledge Discovery, Knowledge Engineering and Knowledge Management. 12th International Conference on Knowledge Engineering and Ontology Development. Budapest, Hungary, 02.11.2020 - 04.11.2020, pp. 250–257.*
with the endpoint beeing available at http://opi.informatik.uni-rostock.de.

## Requirements
OPI is build on the OWL-API and Apache Tomcat. To get the application up and running, install the necessary dependencies using maven. The dockerfile builds the application automatically on startup time, so use 

## Using OPI
To evaluate an ontology from a web source, one can use a get request on the endpoint http://opi.informatik.uni-rostock.de/api with the parameter `?url` pointing to the ontological resource. If you run it locally, replace `opi.informatik.uni-rostock.de` with the given `localhost` location. The full request should look like the following example for a query to the friend of a friend ontology:

http://opi.informatik.uni-rostock.de/api?url=http://xmlns.com/foaf/spec/20140114.rdf

For assessing a local ontology that is not available at a web resource, it is possible to use a post request on the same endpoint (http://opi.informatik.uni-rostock.de/api). The ontology is expected in the request body. The response is printed in a JSON-serialization.

If the target ontology is not consistent with an RDF syntax or one of its extensions like OWL or RDF(S), the service throws an HTTP 400 error and returns an JSON consisting of further information regarding possible causes.