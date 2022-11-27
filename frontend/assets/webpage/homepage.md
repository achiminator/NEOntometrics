Welcome to the *NEOntometrics* application. *NEOntometrics* enables you to explore and calculate ontology measurements. It supports various metrics proposed by the literature or given through the underlying owl-based structure. All public ontology resources available on the web can be utilized. *NEOntometrics* further allows the calculation of historic ontology metrics based on git repositories to enable a broader view of the underlying development processes. We believe it has the potential to become the single-stop resource for making ontology metrics accessible.

The following things are possible with *NEOntometrics*
- **Explore Ontology Metrics**
    - Use the Metric Explorer to learn about the measurable characteristics of an ontology
    - See how the various frameworks have utilized the measurable ontology attributes
    - Explore the differences and commonalities of the metric frameworks.
- **Calculate Ontology Metrics**
    - Put the explored metrics into use. Calculate many of the proposed metrics.
    - Utilize different ontology sources
        - Git-repositories
        - PublicoOntology endpoints
    - Download a CSV for further processing
    - Use a public available GraphQL interface for metric integration in your application.
- **Contribute!** NEOntometrics is ***Open Source!***
    - Customize the application based on your needs
    - Propose new metrics
    - Integrate further ontology sources

And of course, if you have any questions, inquiries, or ideas, feel free to contact me.


# Using the application

## The Metric explorer
The Metric Explorer informs on available measurable features of the ontology_ and is a repository of available metrics in NEOntometrics and beyond, with the two main categories _Elemental Metrics_ and _Quality Framworks_. It provides knowledge of the measurable attributes of ontologies and published metric frameworks. A click on the ℹ️-icon on the left opens the details page of the given item. The _Metric Explorer_ has two main elements.


The **Elemental Metrics** contain the atomic attributes of an ontology that we can measure and that are stored in the database. Examples are graph-related attributes like _depth_ or _breath_, or owl-related _axioms count_, like the number of `owl:equivalentTo` relationships, or the number of transitive object properties.

These elements come with explanations of the underlying measured attributes and, at times, link to further reads. The NEOntometrics authors maintain the elemental metrics and their descriptions. Not all of the elements are implemented (yet). If they are, they have an _implementation name_ connected. The _implementation name_ is the machine-readable identifier, e.g., for the GraphQL interface.

The **Quality Frameworks** are the metric frameworks proposed in the literature. The top-level item of these elements points to the originating resource from which all of their human-readable explanations originate. The underlying metric literature for these frameworks has a great variety in their level of detail. Some describe their measures extensively, including an interpretation, while others only propose the metric. The availability of explanations in the metric explorer mirrors this heterogeneity.

The calculation of the quality frameworks metrics is formalized by linking them to the _Elemental_Metrics_. This linkage homogenizes the metric measures: Often, the quality frameworks proposed by authors of distant countries and research backgrounds use different symbols to describe the same concepts. Here, the _Elemental_Metrics_ provide a common language for the ontology attributes. The field _calculation_ is filled if

 

## Calculating Ontology Metrics

The core functionality of NEOntometrics, the metric calculation, is accessible via the _Calculation_-Tab. The application can either analyze single ontology files or git-based repositories.

1.  At first, a user needs to select the ontology metrics that are important to him. One can choose the essential attributes from the _Elemental Metrics_ or measures that quality frameworks like OntoQA have proposed. Hovering over the elements shows additional information identically to parts of the Metric Explorer. It is possible to select all metrics from a given category by clicking the checkbox on the right or choosing individual ones.
2.  NEOntometrics comes with an inference engine based on [HermiT](http://www.hermit-reasoner.com/). Using NEOntometrics and a reasoner unveils how much and which aspects are implicit in the ontology. However, using the reasoner requires much computational power and is deactivated for ontologies smaller than 0.3 MB.
3.  The text field at the bottom of the page points to the ontology or repository that should be analyzed. You can put any public available git repository or ontology file in this text box. To explore already calculated ontologies, the _Already Calculated_ button shows the repositories already in the database. These repositories can be a starting point for further exploration.
4.  A click on the arrow starts the metric request. If the metric is unknown in the system, the application asks to queue the calculation task. If it is already in the queue, a notification informs of the progress.

Once the data is analyzed, a click on the arrow leads to the metric results presented as a paginated table, representing the measured values for the different ontology versions. The drop-down menu in the header allows for selecting the various ontology files, and the download button ⬇️ exports the metrics into a .csv-file. The update button 🔂 puts the metric into the queue again and retrieves the new version (for git-based repositories).


  ## Using the Calculation-Frontend

The tab _Calculation Engine_ is the main entry point for the metrics calculation. Hovering over the elements shows additional information identically to parts of the M_etric Explorer_. Checking the box on the right enables the calculation of all metrics of a given category. However, adding additional metrics is also possible by clicking on the metric items. The _Already Calculated_ button shows the repositories already in the database. These repositories can be a starting point for further exploration.

A click on the arrow starts the metric request. If the metric is unknown in the system, the application asks to queue the calculation task. If it is already in the queue, a notification informs of the progress. Once the data is analyzed, a click on the arrow leads to the metric results presented as a paginated table, representing the measured values for the different ontology versions. The drop-down menu in the header allows for selecting the various ontology files, and the download button exports the metrics into a .csv.



## Using the API

The GraphQL endpoint is available on [api.neontometrics.informatik.uni-rostock.de/graphql](http://api.neontometrics.informatik.uni-rostock.de/graphql_), accessible through a browser on a GraphiQL interface, or by using any other GraphQL client. Typically, one would first query the node _queueInformation_ to check whether the data is already in the database or the calculation queue, then run a mutation to put the data into the queue or fetch the data from the node _getRepository_.

The GraphQL endpoints further provide documentation on the various available requests and possible return values, thus enabling the guided development of new queries.

# The Architecture of the Calculation

## The Architecture of the Metric Calculation

One design goal was to create a flexible application for integrating new metrics. A researcher shall be able to adapt the application to their individual needs and quickly implement the required metrics. To achieve this adaptability, we did not directly implement the metrics of the various frameworks but decomposed them into their building blocks. For example, the software does not calculate the metric _axiom/class ratio_ at the time of the ontology analysis. Instead, their building blocks _axioms_ and _classes_ are saved in the database. The compositional values are then calculated at the time of querying.

  Fig. 1 presents an example of this kind of decomposition and its representation in the ontology. `Elemental Metrics` contains the atomic elements that the modeled `Metric Frameworks` use. For ``OntoQA Class Inheritance Richness``, the ``Elementary_Metrics`` are ``Classes`` (the number of classes) and ``Sub_Class_Declarations``. The ontology further specifies mathematical relationships between the metrics. In the example, ``_OntoQA_Class_Inheritance_Richness subClassOf (divisor only Classes) and (numerator only Sub_Class_Declarations)_``.

The ``Elementary_Metrics`` are connected to metric instances named identically to the implementation names in the calculation service and the elements in the database. In the example of the ``Sub_Class_Declarations``, this element has a relationship ``implementedBy value subClassOfAxioms``.

![](resource:assets/webpage/ontology.png)

**Fig. 1.** Excerpt of the Metric Ontology. The given _OntoQA_ metric is stored as `OntoQA_Class_Inheritance_Richness subClassOf (divisor only Classes) and (numerator only Sub_Class_Declarations)`.

Further, all elements have rich annotations, providing human-centered meaning to the metrics. The `Elemental Metrics` have descriptions of the kind of measured attributes. The `Metric Frameworks` contain  the information from the corresponding papers. Additionally, links to further online resources or scientific publications are provided. These annotations are the foundation for the *Metric Explorer*, where users find help on the application.

All the information on ontology metrics is formalized in a metric ontology. You can find more details on the functionality and how to extend the metric ontology in the paper:

>_Reiz, Achim; Sandkuhl, Kurt (2022 - 2022): An Ontology for Ontology Metrics: Creating a Shared Understanding of Measurable Attributes for Humans and Machines. In : Proceedings of the 14th International Joint Conference on Knowledge Discovery, Knowledge Engineering and Knowledge Management. 14th International Conference on Knowledge Engineering and Ontology Development. Valletta, Malta, 10/24/2022 - 10/26/2022: SCITEPRESS - Science and Technology Publications, pp. 193–199._


## The Architecture Of Application

The application is based on a dockerized microservice architecture and consists of five components: the calculation-unit **OPI** (Ontology Programming Interface), the API, the Worker application, a database for storing the calculated metrics, and a Redis interface for queueing jobs.

The **Frontend** provides the GUI and receives the requests by the users. It is written using the muli-platform UI language _flutter_ with its underlying client language dart. Upon a new request, the frontend first queries the API for available ontology metrics based on the metric ontology, which fills the help section _Metric Explorer_, where users can inform themselves on the various available metrics and the options for the calculation page.

The **API** is the _django_-based endpoint for accessing calculated metric data or requesting the analysis of new repositories. During the startup of the software, the API queries the metric ontology. It builds the Metric Explorer data for the frontend and creates calculation code dynamically to provide the measurements of the frameworks that build upon the elemental metrics.

After startup, a user can use GraphQL first to check whether the requested data exists already in the database. If so, he can retrieve all the selected metrics for a given repository. If not, it is possible to put the calculation in the queue and track its progress.

The **worker** is responsible for the calculation of the metrics itself. It checks whether jobs are available in the **scheduler** Redis database. If so, it starts the analysis by downloading the repository, then iterating through every file and commit, analyzing the owl ontologies using the **OPI** metric calculation endpoint. Afterward, the calculation results are stored in the **database**. The scheduling mechanism is based on _django-rq. Even though it shares a code base with the api, it runs as a separate application. The number of parallel calculations can be scaled by increasing the number of workers.

![](resource:assets/webpage/SequenceDiagram.jpg)

**Fig. 2.** The process of analyzing and retrieving ontologies with NEOntometrics.

The calculation service **OPI** is responsible for calculating metrics out of ontology documents. While it is based on the calculation service of [OntoMetrics](http://opi.informatik.uni-rostock.de), most underlying code has been replaced. The old application struggled with ontology files larger than 10 MB due to inefficient memory allocation, had no separation of the calculation of the Elemental Metrics, and the composed metrics of the metric frameworks, and lacked support for reasoning. The old application was designed as a standalone application, while the new calculation engine is hidden from the user and only accessed by the **API**.

The backend application utilizes two languages: The **API** is written in python, **OPI** engine builds on Java. While this adds complexity to the application design, it allows the integration and use of the OWL API. This library provides many convenient functions for handling OWL and RDF-Files like an entity searcher or the automatic calculation of some axiom-based metrics.

