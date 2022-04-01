# NEOntometrics
Calculating Ontology Metrics Online
_______

Welcome to the *NEOntometrics* application. *NEOntometrics* enables you to explore and calculate ontology measurements. It implemented manigfold metrics proposed by the literature and much more. All public ontology ressources available on the web can be utilized. *NEOntometrics* is further possible to calculate historic ontology metrics based on git repositories, to enable more broader view on the development process of ontologies. It is targeted to be the single-stop resource for making ontology metrics accessible.

The following things are possible with *NEOntometrics*
- Explore Ontoloy Metrics
    - Use the Metric Explorer to learn about the measurable characteristics of an ontology.
    - See how the various measurement points have been utilized by the available frameworks-
    - Explore the differencies and commonalities of the metric frameworks.
    - Read about the quality characteristics some of the frameworks have proposed and which metrics influence them.
- Calculate Ontology Metrics
    - Put the explored metrics into use. Calulate many of the proposed metrics.
    - Utilize different ontology sources
        - Git- repositories
        - Public Ontology endpoints
    - Download a CSV for further processing
    - Use a public available REST & GraphQL interface for metric integration in your applications.
- Contribute! NEOntometrics is **Open Source!**
    - Customize the application based on your needs
    - Propose new metrics
    - Integrate further ontology sources

And of courese, if you have any questions, inquiries or ideas, feel free to contact me [Achim Reiz](mailto:achim.reiz@uni-rostock.de).

# Using NEOntometrics
Our proposed approach targets to solve many of the questions named above. The software provides a state-of-the-art user experience, provides an endpoint for many metric frameworks while beeing easily extensible through the use of an ontology for setting up these metrics, and is fully open source.

The following section details the software itself: it presents the different components of the service, how they interact and the underlying development decisions. Here, we also present how our ontology based metric compositions are extensible for future usages. Afterwards, give an overview on how to put the software to use.
## The Architecture of the Calculation

  ### The Architecture of the Metric Calculation

Our design goal was to create an application that is flexible regarding the integration of new metrics. A researcher shall have the possibility to adapt the application to their individual needs, and to easily implement the individual metrics that they need. To achieve this adaptability, we did not implement the metrics of the various frameworks directly, but decomposed them first and then reused as much as possible, with a set up provided by an ontology.

Fig. 1 presents an example for this kind of decomposition. The ontology is analyzed for the atomar elements that build the further compositions, here named _Elementary\_Metrics_. In the example of _OntoQA- Class Inheritance Richness_, the _Elementary\_Metrics_ are _Classes_ (the number of classes) and _Sub\_Class\_Declarations_. The ontology further specifies mathematical relationships between the metrics, in the given example, _OntoQA\_Class\_Inheritance\_Richness__isEquivalentTo (divisor only Classes) and (numerator only Sub\_Class\_Declarations)_.

The _Elementary\_Metrics_ are connected to metric instances that are named identically to the implementation names in the calculation service and the elements in the database. In the example of the _Sub\_Class\_Declarations_, this element has relationship _implementedBy value subClassOfAxioms_. This allows for the usage of new metrics without the need to perform a new calculation.

![](resource:webpage/ontology.png)

**Fig. 1.** Excerpt of the Metric Ontology.

Further, all of the elements have rich annotations, providing human centered meaning of the metrics. The elementary metrics come with descriptions on what they measures, the metrics of the framworks are annotated with the information out of the corresponding papers. Additionally, links to further online ressources or the scientific publications are provided. These annotation are the foundation for the _Metric Explorer_, where users find help on the application.

  ### The Architecture Of Application

The application is based on a dockerized microservice architecture and consists of five components: the calculation-unit **OPI** (Ontology Programming Interface), the API, the Worker application, a database for storing the calculated metrics and a redis interface for queueing jobs.

The **Frontend** provides the GUI and receives the requests by the users. It is written using the muli-platform UI language _flutter_ with its underlying client language dart. Upon a new request, the frontend first queries the API for available ontology metrics based on the metric ontology. This data fills the help section _Metric Explorer_, where users can inform themselves on the various available metrics, as well as the options for the calculation page, which allows display already calculated metrics and to initiate a new calculation.

The **API** is the _django_-based endpoint for accessing already calculated metric data or requesting the analysis of new repositories. During the startup of the software, the API queries the metric ontology. It builds the Metric Explorer data for the frontend and creates calculation code dynamically to provide the measurements of the frameworks that build upon the elemental metrics.

After startup, a user can use GraphQL to first check whether the data he requests exists already in the database. If so, he can retrieve all the selected metrics for a given repository. If not, it is possible to put the calculation of a given repository in the queue and track its progress.

The **worker** is responsible for the calculation of the metrics itself. It checks wheteher jobs are available in the **scheduler** redis database. If so, it starts the analysis by first downloading the repository, then iterating through every file and commit, analyzing the owl ontologies using the **OPI** metric calulation endpoint. Afterwards, the calculation results are stored in the **database**. The scheduling mechanism is based on _django-rq. Even though it shares a code base with the api, it runs as a separate application. The number of parallel calculations can be scaled by increasing the number of workers.

![](resource:webpage/sequenceDiagram.jpg)

**Fig. 2.** The process of analyzing and retrieving ontologies with NEOntometrics.

The calculation service **OPI** is responsible for calculating metrics out of ontology documents. While it is based on the calculation service published in [19], most of the underlying code has been replaced. The old application struggled with ontology files larger than 10 MB due to inefficient memory allocation, had no seperation of the calulation of the _Elemental Metrics_ and composed metrics of the metric frameworks and lacked support for reasoning. The old application was designed as a standalone application, while the new calculation engine is hidden for the user and only accessed by the **API**.

The backend application utilizes two languages: The **API** is written in python, **OPI** engine builds on Java. While this adds complexity regarding the application design, it allows to integrate and use of the OWLAPI. This library provides many convenience functions for handling OWL and RDF-Files like an entity searcher or the automatic calculation of some of the axiom-based metrics.

  # Using NEOntometrics
  ## The Metric explorer

The page _Metric Explorer_ is an repository on available metrics in NEOntometrics and beyond. It two main categories are _Elemental Metrics_, which are the underlying, atomar measurements that are calculated on the ontologies itself. All information shown in this category originates from the authors of the software. _Quality Frameworks_, in opposite,presents the ontology quality metrics developed by other researchers, like the OntoQA Metrics [10] by Tartir et. al shown in Fig. 3. Here, all information originates from the authos of the given frameworks.

The page provides information on five categories (though not all of them are filled for all the metrics). _Metric Definition_ contains the formal definiton of the metrics and how they are caluclated. _Metric Description_ supplements a more human readable explonation, at times also an example. _Metric Interpretation_ guides the practical usage. _Calculation_ provides their decompositions into the _Elemental Metrics_ and _seeAlso_ links to further ressources like the corresponding papers or additional reads.

## Using the Calculation-Frontend

The tab _Calculation Engine_ is the main entrypoint for the selection of the metrics. Howering over the elements shows additional information identically to parts of the M_etric Explorer_. Checking the box on the right enables the calculation of all metrics of a given category. However, it is also possible to add further, isolated metrics by clicking on the metric items. The button *"Already Calculated"*; shows the repositories which metrics are already in the database. These repositories can be a starting point for further exploration.

A click on the arrow starts the metric request. If the metric is not yet stored in the system, the application asks to put the calculation task into the queue. If it is already in the queue, a notification informs on the progress. As soon as the data is completely analyzed, a click on the arrow leads to the metric results presented as a paginated table, representing the metric values along the different ontology versions. The drop down menu in the header allows for the selection of the various ontology metrics, the download button exports the metrics of the file into a .csv.

  ## Using the API

The graphQL endpoint is available on _/graphql_ , accessible through a browser on a GraphiQL interface or by using any other GraphQL client. Typically, one would first query the node _queueInformation_ to check whether the data is already in the database or in the calculation queue, then either run a mutation to put the data into the queue or fetch the data from the node _getRepository_. Using the GraphiQL interface, it is possible to iteratively develop the query while exploring the query-schema.
