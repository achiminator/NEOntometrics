package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.metrics.LogicalAxiomCount;
import org.semanticweb.owlapi.model.OWLOntology;

public class CountLogicalAxiomsMetric extends BaseMetric {
	
	public CountLogicalAxiomsMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		//MLIC new implemented
		return (new LogicalAxiomCount(ontology).getValue());
	}

	@Override
	public String toString() {
		return "Logical axioms count";
	}

}
