package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class AbsoluteRootCardinalityMetric extends GraphMetric {

	public AbsoluteRootCardinalityMetric(OWLOntology pOntology) {
		super(pOntology);
	}
	
	 @Override
	    public Integer getValue() {
			return parser.getRoots().size(); //with imports
	    }

	    @Override
	    public String toString() {
		return "Absolute root cardinality";
	    }
}
