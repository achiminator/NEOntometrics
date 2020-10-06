package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.metrics.HiddenGCICount;
import org.semanticweb.owlapi.model.OWLOntology;

public class CountHiddenGCIMetric extends ClassAxoimsMetric {

	int result = 0;

	public CountHiddenGCIMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// MLIC new implemented
		return (new HiddenGCICount(ontology).getValue());
	}

	@Override
	public String toString() {
		return "HiddenGCICount";
	}

}
