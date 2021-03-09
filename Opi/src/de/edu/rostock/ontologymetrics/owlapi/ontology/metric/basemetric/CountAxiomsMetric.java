package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.metrics.AxiomCount;
import org.semanticweb.owlapi.model.OWLOntology;

public class CountAxiomsMetric extends BaseMetric {

    public CountAxiomsMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
    // MLIC new implemented
	return (new AxiomCount(ontology).getValue());
    }

    @Override
    public String toString() {
	return "Axioms";
    }

}
