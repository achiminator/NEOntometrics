# NEOntometrics Ontology Calculation software

Welcome to **NEOntometrics**. It offers a flexible endpoint for calculating ontology metrics online, either directly from an online ressource or a git-based repository. 
You can use the service online on neontometrics.com, or visit api.informatik.uni-rostock.de for the GraphQL api. Here you can also find information on how the service functions and 

The scientific papers that further describe the service are currently under review. As soon as they are accepted, we will provide them here as well.

## Implementing the Service locally
To run a local installation, first **Change the Password in the Docker-File**, and chose how much the service shall be scalled. The scalling is handled via the number of worker instances. 

Then, first build the *NEOntometrics-API*, *Worker* and *OPI*: `docker compose build`
Afterwards, start the service with: `docker compose up -d `

