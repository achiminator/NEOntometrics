# The Git Extension for OPI
The Git-Extension is responsible for 

1. Receiving a URL to a git-based repository (and file, if you wish)
2. Cloning the Repository
3. Sending the Data to the opi-backend for metric calculation
4. Storing the data in the database
5. Retrieving the data from the database.

As the metric caluclation (especially for a whole repository) can take some time, the service is build **asynchronous**. It first checks whether a given request was performed before - if that is the case, the data is retrieved from the database. If not, the caluculation is put in the queue. The user can poll the service and gets the queue-position for his job, until the job is done and the metrics are displayed.
If you're only interested in USING the serivce, please skip to [Using the Service](#Using)


## Requirements
The Application fits into the NEONTOMETRIC-Suite and depends on various micro-services. See the readme in the root-directory for further instructions. The following instruction is for the independent instance of the Git-Extension on a local machine.

Please install the python dependencies of the pipfile file using `pipenv`. Further, it is necessary to add a database as specified in the `ontoMetricsAPI/settings.py` file. For testing and developing purposes, it is possible to use a SQLite database. Don't forget to run the django-migrate command and don't commit this database to the git repository.

For the asynchronous Scheduling, it is further necessary to run a REDIS-Database (e.g. from Docker). For development and debugging, the asynchronous behaviour can be disabled in the settings-file.

## Run the Service
If a redis instance is up and running, you can start the application. First start the `pipenv shell`, then the main django instance through `py manage.py runserver`. As a last step (*if the async-behavior is not disabled by setting the flag in the `settings.py`-file* ), the worker needs to be started by `py manage.py rqworker`. Please be aware that the worker does not function in a windows environment. However, it functions under `WSL`. `py manage.py rqstats` gives you information on the current load of the system (*number of jobs in the queue, number of workers*)


## Using GraphQL Endpoint
The Endpoint is available on localhost:8086/graphql. Use the interactive GraphQL documentation to learn how what kind of analysis is possible. A sample query for an ontology is shown down below.

    getRepository(repository: "https://github.com/kbarber/puppet-ontologies") {
        edges {
            node {
                created
                repository
                fileName
                branch
                wholeRepositoryAnalyzed
                metrics {
                    edges {
                        node {
                            CommitTime
                            CommitID
                            CommitMessage
                            AuthorEmail
                            AuthorName
                            CommiterEmail
                            CommitterName
                            Size
                            ReadingError
                            axioms
                            classes
                            individuals
                            logicalAxioms
                            subClassOfAxioms
                            anonymousClasses
                            generalAnnotationAxioms
                            hiddengciCount
                            inverseFunctionalObjectPropertyAxioms
                            inverseObjectPropertyAxioms
                            irreflexiveObjectPropertyAxioms
                            objectProperties
                            objectPropertiesOnClasses
                            objectPropertyAssertionaxioms
                            objectPropertyDomainAxioms
                            objectPropertyRangeAxioms
                            OQual_Absolute_Breath
                            OQual_Absolute_Depth
                            OQual_Absolute_Leaf_Cardinality
                            OQual_Absolute_Sibling_Cardinality
                            OQual_Anonymous_classes_ratio
                            OQual_Average_Breath
                            OQual_Average_Depth
                            OQual_Average_Sibling_FanOutness
                            OQual_Axiomclass_ratio
                            OQual_Class_relation_ratio
                            OQual_Generic_complexity
                            OQual_Inverse_relations_ratio
                            OQual_Maximal_Breath
                            OQual_Maximal_Depth
                            OQual_Maximal_Leaf_FanOutness
                            OQual_Maximal_Sibling_FanOutness
                            OntoQA_Attribute_Richness
                            OntoQA_Class_Inheritance_Richness
                            OntoQA_Class_Utilization
                            OntoQA_Cohesion
                            OntoQA_Inheritance_Richness
                            OntoQA_Relationship_Diversity
                            OntoQA_Relationship_Richness
                            OntoQA_Schema_Deepness
                            ComplexityMetrics_Average_Paths_Per_Concept
                            ComplexityMetrics_Average_Relationships_Per_Concept 
                
                        }
                    }
                }
            }
            }
        }
    }