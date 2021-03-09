package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.metrics.GCICount;
import org.semanticweb.owlapi.model.OWLOntology;

public class CountGCIMetric extends ClassAxoimsMetric {

	int result = 0;

	public CountGCIMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// MLIC new implemented
		return (new GCICount(ontology).getValue());
	}

	@Override
	public String toString() {
		return "GCICount";
	}

}
