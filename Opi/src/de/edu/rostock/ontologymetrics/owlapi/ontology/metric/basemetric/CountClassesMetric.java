package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class CountClassesMetric extends BaseMetric {

    public CountClassesMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
    	// the owl:Thing inclusive
	return ontology.getClassesInSignature().size();
    }

    @Override
    public String toString() {
	return "Class count";
    }

}
