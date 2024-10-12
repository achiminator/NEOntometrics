# The Git Extension for OPI
The Git-Extension is responsible for 

1. Receiving a URL to a git-based repository or ontology file
2. Cloning the Repository
3. Sending the Data to the opi-backend for metric calculation
4. Storing the data in the database
6. Retrieving the data from the database.

As the metric calculation (especially for a whole repository) can take some time, the service is build **asynchronous**. The Git-Extension service provides means for 

1. retrieving data that iis already calculated
2. Querying and manipulating the queue.


## Requirements
The Application fits into the NEONTOMETRICS-Suite and depends on various micro-services. See the readme in the root-directory for further instructions. The following instructions are targeted at developers for setting up an independent instance of the Git Extension on a local machine for development purposes.

Please install the python dependencies of the pipfile file using `pipenv`. Further, adding a database as specified in the `ontoMetricsAPI/settings.py` file is necessary. The service won't start without having a relational database up and running; `opi` is required for metric calculation. A `redis` instance is needed to handle the scheduling and queueing (for development and debugging, the asynchronous behaviour can be disabled in the settings-file). To get the underlying services up and running, it is best to start the docker service (see root-readme).

## Run the Service
If a Redis instance is up and running, and the database is set up (either using SQLite or MariaDB/MySQL; the best is to use the provided docker service), you can start the application. First start the `pipenv shell`, then the main django instance through `py manage.py runserver`. As a last step (*if the async-behavior is not disabled by setting the flag in the `settings.py`-file* ), the worker needs to be started by `py manage.py rqworker`. Please be aware that the worker does not function in a Windows environment. However, it functions under `WSL` (*Windows Subsystem for Linux*). `py manage.py rqstats` gives you information on the current load of the system (*number of jobs in the queue, number of workers*).

The admin interface on http://localhost:8080/admin provides sites for managing the queue and accessing and manipulating the data stored in the database. However, after the system is up and running, one first needs to create an admin account using `py manage.py createsuperuser`. 


## Using GraphQL Endpoint
The Endpoint is available on localhost:8080/graphql. Use the interactive GraphQL documentation to learn how what kind of analysis is possible. A sample query for an ontology is shown down below.

    {
        getRepository(repository: "github.com/biobanking/biobanking") {
            edges {
            node {
                repository
                ontologyfile_set {
                edges {
                    node {
                    id
                    fileName
                    branch
                    commit {
                        edges {
                        node {
                            pk
                            Size
                            AuthorEmail
                            anonymousClasses
                        }
                        }
                    }
                    }
                }
                }
            }
            }
        }
    }
