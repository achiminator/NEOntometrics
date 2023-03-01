# Analyzing with NEOntometrics

The application can either analyze single ontology files or git-based repositories.

1.  At first, a user needs to select the ontology metrics that are important to him. One can choose the essential attributes from the _Elemental Metrics_ or measures that quality frameworks like OntoQA have proposed. Hovering over the elements shows additional information identically to parts of the Metric Explorer. It is possible to select all metrics from a given category by clicking the checkbox on the right or choosing individual ones.
2.  NEOntometrics comes with an inference engine based on [HermiT](http://www.hermit-reasoner.com/). Using NEOntometrics and a reasoner unveils how much and which aspects are implicit in the ontology. However, using the reasoner requires much computational power and is deactivated for ontologies smaller than 0.3 MB.
3.  The text field at the bottom of the page points to the ontology or repository that should be analyzed. You can put any public available git repository or ontology file in this text box. To explore already calculated ontologies, the _Already Calculated_ button shows the repositories already in the database. These repositories can be a starting point for further exploration.
4.  A click on the arrow starts the metric request. If the metric is unknown in the system, the application asks to queue the calculation task. If it is already in the queue, a notification informs of the progress.

Once the data is analyzed, a click on the arrow leads to the metric results presented as a paginated table, representing the measured values for the different ontology versions. The drop-down menu in the header allows for selecting the various ontology files, and the download button ⬇️ exports the metrics into a .csv-file. The update button 🔂 puts the metric into the queue again and retrieves the new version (for git-based repositories).
