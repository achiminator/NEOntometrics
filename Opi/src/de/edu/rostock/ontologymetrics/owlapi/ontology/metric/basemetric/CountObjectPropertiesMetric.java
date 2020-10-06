package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class CountObjectPropertiesMetric extends BaseMetric {

    public CountObjectPropertiesMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
	// the owl:ObjectProperty inclusive
	return ontology.getObjectPropertiesInSignature().size();
    }

    @Override
    public String toString() {
	return "Object property count";
    }

}
