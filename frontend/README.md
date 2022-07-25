# neonto_frontend

*The frontend for the NEOntometrics application.*

The frontend is written in the multiplattform language flutter and compiled via docker compose to a web project. On starting time, it calls the [API](http://api.neontometrics.informatik.uni-rostock.de/metricexplorer) for information on metrics that are up for calculation, which are then dynamically displayed on the pages *Metric Explorer* and *Calculation Engine*.

If one starts a request, the frontend first
1. Performs a queue-request to check whether a repository or ontology file is already in the Database or queue
2. If the metric is already in the  queue, but not yet finished, the queue information is returned. If it is not yet known in the system, the user can put the repository or file in the queue to be analyzed.
3. If the metrics are already calculated in the database, the frontend builds a GraphQL call with the selected metrics and retrieves the ontology metrics.
4. The selected Metrics are displayed on the frontend. 
