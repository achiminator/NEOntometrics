# SemOI
Semantically Augmentation for Open Images
_______

The application builds upon a microservice architecture. In total, the service consists of three parts: The frontend and image-recognition container (here called ***webapp***), the semantic augmentation web-service (here called ***semanticAPI***), and a triplestore ([Apache Jena Fuseki](https://jena.apache.org/documentation/fuseki2/) based on an Docker Image of [Stain](https://registry.hub.docker.com/r/stain/jena-fuseki)).

The former serves the webpage, stores the picture temporarily, and performs the object detection using TensorFlow-hub with a pre-trained model for Open Images. In the next step, the detected objects are transferred to the semantic advisory component. This component performs a SPARQL-request for every detected object on an apache fuseki server. Based on the inferred objects, the SC-value is calculated for each detected element and returned to the fronted to display the results.
The code is written in python and utilizes the Django- and Django-Rest-Framework and Docker  for containerization. For ontology development, we used the tool Protégé. Further, the code is published under an open-source license (LGPL) on Github , including the dockerfile for the service orchestration and the ontology-file.
**Before using the docker-compose file, please do not forget to change the password**

Further, we developed a translator the conversion of the json-based hierachy provided by OpenImages into an RDF-graph. Please see SemanticAPI/ImageRecog2RDF for the sourcefiles.

# Setting up a development environment

## Initial setup after clone

- run ``$ pipenv install`` in each subdirectory to install any dependencies.

## Running the development server

1. run ``$ pipenv shell`` in the desired subdirectory (e.g. "WebApp").
2. run ``$ python manage.py runserver [port]`` in the previously started shell.

Any file changes will cause a reload of the project.
