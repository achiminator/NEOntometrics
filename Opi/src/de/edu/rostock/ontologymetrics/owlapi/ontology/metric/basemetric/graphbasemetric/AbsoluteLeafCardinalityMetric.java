package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class AbsoluteLeafCardinalityMetric extends GraphMetric {

	public AbsoluteLeafCardinalityMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	public Integer getValue() {
		return parser.getLeaveClasses().size();
	}

	public String toString() {
		return "Absolute leaf cardinality";
	}
}
