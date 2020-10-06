# OntoMetrics Suite

The following application consits of (so far) 2 Elements
- The calculation engine *(opi)*
- The Git Extension

The following contains a short description of both services. For a more detailed perspective visit the subfolders. There you'll find additional readme documents containing more information.
## OPI
The ontology programming interface takes an ontology via `POST` or a link to a ontoloy via `GET` and calculates the Metrics known from the [OntoMetrics Platform](ontometrics.informatik.uni-rostock.de).

## Git Extension
This extension written in Python using the Django Rest Framework. It accepts a public git url, clones the repository and calculates the Ontology measurment for every commit of the selected file using the OPI-Api.