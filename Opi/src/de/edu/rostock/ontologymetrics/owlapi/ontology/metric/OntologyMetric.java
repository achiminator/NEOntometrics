package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.GraphParser;

public abstract class OntologyMetric {


    protected final Logger myLogger = Logger.getLogger(this.getClass());

    public OntologyMetric() {
	
    }
    public abstract Map<String, Object> getAllMetrics(OWLOntology ontology, GraphParser parser, GraphParser parserI);
}
