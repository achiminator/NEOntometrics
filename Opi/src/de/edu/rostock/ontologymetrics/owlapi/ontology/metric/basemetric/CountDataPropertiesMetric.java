package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class CountDataPropertiesMetric extends BaseMetric {

    public CountDataPropertiesMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
	// the owl:TopDataProperty inclusive
	return ontology.getDataPropertiesInSignature().size();
    }

    @Override
    public String toString() {
	return "Data property count";
    }

}
