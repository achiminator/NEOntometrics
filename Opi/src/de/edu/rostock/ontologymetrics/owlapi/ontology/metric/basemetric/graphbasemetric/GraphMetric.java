package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

abstract class GraphMetric extends GraphBaseMetric {

    public GraphMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Object getValue() {
	return null;
    }

    @Override
    public String toString() {
	return "Graph metric";
    }

}
