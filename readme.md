
# NEOntometrics Ontology Calculation software

Welcome to **NEOntometrics**. It offers a flexible endpoint for calculating ontology metrics online, directly from an online resource or a git-based repository. You can use the service online on neontometrics.com or visit api.informatik.uni-rostock.de for the GraphQL API. Here you can also find information on how the service functions and

The scientific papers that further describe the service are currently under review. As soon as they are accepted, I will provide them here as well.

## Getting Help
If you encounter any problems, please do not hesitate to reach out. For bugs and issues that occur during deploying the service yourself or developing it further, it is probably best to create an issue here on Github. I'm also stoked for new features or to help people get started in their developments.

For questions regarding the measurements themselves or a discussion on the tool, you can also reach me well on Linkedin: https://www.linkedin.com/in/reiz/

# Getting NEOntometrics

## Using the Service Online

NEOntometrics is available online at http://neontometrics.com.

## Implementing the Service Locally

NEOntometrics is based on a variety of Micro-Services orchestrated using docker. An up-to-date docker installation that includes docker-compose is mandatory for running the software.

1.  At first, clone the repository to the future locale location. Pass the flag `-c core.longpaths=true ` to allow for longer file names, thus:
 `git clone -c core.longpaths=true https://github.com/achiminator/NEOntometrics`
2.  Edit the docker-compose.yml file and **change** the **password**. The password secures the communication between the database and the other micro services.
3.  in `frontend/lib/settings.dart`, replace the url for `final String apiUrl = "http://api.neontometrics.informatik.uni-rostock.de";` with the URL to your servers. Otherwise, the GUIwill still query use our backend. For running it locally, set it to `http://localhost:8086`.
4.  To launch the service, first build the _NEOntometrics-API_, _Worker_, and _OPI_ services with `docker-compose build`
5.  If the build run successful and the images are created, you can start the services with: docker compose up -d
6.  That's it. The server should be up and running. The locations are available at the ports specified in the docker file. Happy Evaluations!

  

# How to use NEOntometrics

The application has two main parts: The metric explorer, an interactive collection of measurable ontology attributes to assist users in selecting the right metrics, and the calculation engine for performing the calculation itself. The following section gives an overview of how to use these two services.

## The Metric Explorer

The Metric Explorer informs on available measurable features of the ontology. It provides knowledge of the measurable attributes of ontologies and published metric frameworks. A click on the ℹ️-icon on the left opens the details page of the given item. The _Metric Explorer_ has two main elements.

The **Elemental Metrics** contain the atomic attributes of an ontology that we can measure and that are stored in the database. Examples are graph-related attributes like _depth_ or _breath_, or owl-related _axioms count_, like the number of _owl:equivalentTo_ relationships, or the number of transitive object properties.

These elements come with explanations of the underlying measured attributes and, at times, link to further reads. The NEOntometrics authors maintain the elemental metrics and their descriptions. Not all of the elements are implemented (yet). If they are, they have an _implementation name_ connected. The _implementation name_ is the machine-readable identifier, e.g., for the GraphQL interface.

The **Quality Frameworks** are the metric frameworks proposed in the literature. The top-level item of these elements points to the originating resource from which all of their human-readable explanations originate. The underlying metric literature for these frameworks has a great variety in their level of detail. Some describe their measures extensively, including an interpretation, while others only propose the metric. The availability of explanations in the metric explorer mirrors this heterogeneity.

The calculation of the quality frameworks metrics is formalized by linking them to the _Elemental_Metrics_. This linkage homogenizes the metric measures: Often, the quality frameworks proposed by authors of distant countries and research backgrounds use different symbols to describe the same concepts. Here, the _Elemental_Metrics_ provide a common language for the ontology attributes. The field _calculation_ is filled if

All the information on ontology metrics is formalized in a metric ontology. You can find more details on the functionality and how to extend the metric ontology in the paper:

_Reiz, Achim; Sandkuhl, Kurt (2022 - 2022): An Ontology for Ontology Metrics: Creating a Shared Understanding of Measurable Attributes for Humans and Machines. In : Proceedings of the 14th International Joint Conference on Knowledge Discovery, Knowledge Engineering and Knowledge Management. 14th International Conference on Knowledge Engineering and Ontology Development. Valletta, Malta, 10/24/2022 - 10/26/2022: SCITEPRESS - Science and Technology Publications, pp. 193–199._

  

## Calculating Ontology Metrics

The core functionality of NEOntometrics, the metric calculation, is accessible via the _Calculation_-Tab. The application can either analyze single ontology files or git-based repositories.

1.  At first, a user needs to select the ontology metrics that are important to him. One can choose the essential attributes from the _Elemental Metrics_ or measures that quality frameworks like OntoQA have proposed. Hovering over the elements shows additional information identically to parts of the Metric Explorer. It is possible to select all metrics from a given category by clicking the checkbox on the right or choosing individual ones.
2.  NEOntometrics comes with an inference engine based on [HermiT](http://www.hermit-reasoner.com/). Using NEOntometrics and a reasoner unveils how much and which aspects are implicit in the ontology. However, using the reasoner requires much computational power and is deactivated for ontologies smaller than 0.3 MB.
3.  The text field at the bottom of the page points to the ontology or repository that should be analyzed. You can put any public available git repository or ontology file in this text box. To explore already calculated ontologies, the _Already Calculated_ button shows the repositories already in the database. These repositories can be a starting point for further exploration.
4.  A click on the arrow starts the metric request. If the metric is unknown in the system, the application asks to queue the calculation task. If it is already in the queue, a notification informs of the progress.

Once the data is analyzed, a click on the arrow leads to the metric results presented as a paginated table, representing the measured values for the different ontology versions. The drop-down menu in the header allows for selecting the various ontology files, and the download button ⬇️ exports the metrics into a .csv-file. The update button 🔂 puts the metric into the queue again and retrieves the new version (for git-based repositories).

# How to Extend NEOntometrics

## Adapting NEOntometrics with the Metric Ontology
The metrics are structured by the metric ontology, in `\Git-Extension\rest\metricOntology\OntologyMetrics.owl`. Every change made to the metric ontology is reflected in the application after a restart, thus, allowing to perform speedy adoptions.

The ontology was developed utilizing the open-source software [Protégé](https://protege.stanford.edu/). I recommend using the same software for performing individual changes. 

### Structure of the Ontology
The classes _Elemental_Metrics_ and _Quality_Frameworks_ form the metric calculation's foundation. The _Elemental_Metrics_ reflect the atomic measurements calculated by the OPI-Java application, the _Quality_Framworks_ allow to restructure and use the calculated _Elemental_Metrics_.

The measurements in *Elemental_Metrics* are connected to the calculated OPI metrics and the database fields  using the object property *implementedBy* with a connection to an indidividual. 

The *Quality_Frameworks* reuse the calcualted metrics. For doing so, there are various object properties available, reflecting the primary mathematical calculations like sum, division, and multiplication. Via nesting multiple statements, complex calculations can be mapped.


### Working on Existing Ontology Metrics:
It might be useful to delete, rename or restructure given metrics. That can be achieved by altering the subclass hierachy of the *Quality_Frameworks* in Protégé. Here, it is also possible to create new elements. (I recommend to reuse existing measurements to modify the ontology by an example.)
The ontology supports extension through formalized properties for adding descriptions like (_MetricDefinition_, _MetricDescription_, _MetricInterpretation_) and object properties that link _Quality_Frameworks_ to _Elemental_Metrics_ for the calculation logic.

### Adding new Elemental Metrics
In unique cases, the given set of *Elemental_Metrics* might not be sufficient. For these cases, it is possible to extend the atomic, calculated measurements. However, that is a larger endavor that requires
- A change of the database structure in `Git-Extension/rest/models.py` and a commit of the database changes using the django ORM functions `py manage.py makemigrations rest` and `py manage.py migrate`.
- An implementation of the new measurements in OPI - new measures can be introduced by extending one of the existing `main.java.neontometrics.calc`-packages and save the result in the exsting `returnObject`-map.
- An extension of the metric ontology by adding (1.) a new Elemental Metric and a corresponding individual.
- The name of the individual **MUST** conform exactly to (1.) the name of the field in the database, and (2.) the a JSON key in the return value of the OPI file.

I hope you find NEOntometrics useful for you given use-case. If you have any questions, feel free to reach out either by [mail](mailto:achim.reiz@uni-rostock.de), [LinkedIn](https://www.linkedin.com/in/reiz/) (preferred), or GitHub Issue.
