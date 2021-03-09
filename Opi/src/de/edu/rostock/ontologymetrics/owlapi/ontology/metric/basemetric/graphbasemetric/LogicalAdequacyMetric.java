package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class LogicalAdequacyMetric extends GraphMetric {

    public LogicalAdequacyMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Float getValue() {
	return 0.0F;
    }

    @Override
    public String toString() {
	return "Logical adequacy";
    }

}
