package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

public abstract class OntologyMetric {


    protected final Logger myLogger = Logger.getLogger(this.getClass());

    public OntologyMetric() {
	
    }
//    /**
//     * Returns all Metrics of the selected class. Metrics need to be calculated first!
//     * @return java.util.map
//     */
//    public abstract Map<String, Object> getAllMetrics();
//    /**
//     * calculates all metrics of the class. Returns the same structure as getAllMetrics()
//     * @return java.util.map
//     */
//    public abstract Map<String, Object> calculateAllMetrics(OWLOntology ontology);
}
