package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class CountIndividualsMetric extends BaseMetric {

    public CountIndividualsMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
	return ontology.getIndividualsInSignature().size();
    }

    @Override
    public String toString() {
	return "Individual count";
    }

}
