# Ontology Programming Interface *(Opi*)

## Requirements
OPI is build on the OWL-API and Apache Tomcat. To get the application up and running, install the necessary dependencies using maven.

## Using OPI
To evaluate an ontology from a web source, one can use a get request on the endpoint http://opi.informatik.uni-rostock.de/api with the parameter `?url` pointing to the ontological resource. The full request should look like the following example for a query to the friend of a friend ontology:

http://opi.informatik.uni-rostock.de/api?url=http://xmlns.com/foaf/spec/20140114.rdf

For assessing a local ontology that is not available at a web resource, it is possible to use a post request on the same endpoint (http://opi.informatik.uni-rostock.de/api). The ontology is expected in the request body. The response is printed in an XML serialization using the same top categories and terminology used in the GUI-version and presented in Table 2. The underlying computational engine is the same for the API, as well as for the web-application. Like in the GUI-version, the inclusion of class metrics significantly increases the response time and is therefore disabled by default. If the class-metrics are required, a header key named `classmetrics` with the value `true` enables the calculation.

If the target ontology is not consistent with an RDF syntax or one of its extensions like OWL or RDF(S), the service throws an HTTP 400 error and returns an XML consisting of further information regarding possible causes. 
By default, all assessed ontologies are stored internally at Rostock University for further research purposes. This behavior can be disabled by adding the parameter `save : false` to the header. The response header contains the parameter saved : true if the ontology is stored on the server or `saved : false` if otherwise.
