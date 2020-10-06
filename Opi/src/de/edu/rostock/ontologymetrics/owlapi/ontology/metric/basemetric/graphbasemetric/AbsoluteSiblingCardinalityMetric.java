package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class AbsoluteSiblingCardinalityMetric extends GraphMetric {

	public AbsoluteSiblingCardinalityMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	public Integer getValue() {
		return parser.getSibs().size();
	}
	
	public String toString() {
		return "Absolute sibling cardinality";
	}
}
