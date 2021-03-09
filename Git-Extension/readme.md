# The Git Extension for OPI

## Requirements
Please install the python dependencies of the `requirements.txt` file using `pip`. Further, it is necessary to add a database as specified in the `ontoMetricsAPI/settings.py` file. For testing and developing purposes, it is possible to use a SQLite database. Don't forget to run the django-migrate command and don't commit this database to the git repository.

## Usage
The git-extension is written in python and takes in a Get-Request pointing to the Ontology repository and the target file. The Request looks the following:

`http://localhost:8000/git?classMetrics=True &repository=https://github.com/ESIPFed/sweet&target=src/human.ttl`

3 Input parameters are necessary:
- `classMetrics` is a boolean value indicating whether the ClassMetrics are calculated. Can be omitted and is `false` by default. The calculation is ressource-intensive and increases heavily the response-time
- `repository` points to the git-service and the repository URL
- `target` points to the relative path of the target ontology within the repository.

