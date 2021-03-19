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


## Using

### Input Parameters
The git-extension is written in python and takes in a Get-Request pointing to the Ontology repository and the target file. The Request looks the following:

`http://localhost:8000/git?classMetrics=True &url=https://github.com/ESIPFed/sweet/blob/master/src/humanCommerce.ttl`

- The `URL`- parameter is always necessary. It points to a whole repository or a specific file within a repository.
- `classMetrics` is a boolean value indicating whether the ClassMetrics are calculated. Can be omitted and is `false` by default. The calculation is ressource-intensive and increases heavily the response-time

If the target has been analyzed before, the metric is retrieved from the database and directly displayed. Otherwise, a new calculation job is put into the queue and the rest-interface returns information on the upcomming job - especially the `queuePosition` is interesting, as it indicates how many jobs are to be done before the own job is getting calculated.


`{
    "taskedFinished": false,
    "queuePosition": 0,
    "url": "https://github.com/ESIPFed/sweet/",
    "repository": "/ESIPFed/sweet",
    "service": "github.com"
}`
