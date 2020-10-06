package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class TotalNumberOfPathsMetric extends GraphMetric {

	public TotalNumberOfPathsMetric(OWLOntology pOntology) {
		super(pOntology);
	}
	
	 @Override
	    public Integer getValue() {
			return parserI.getPathsAll().size(); //with imports
	    }

	    @Override
	    public String toString() {
		return "Total number of paths";
	    }
}
