package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class CountInstancesMetric extends BaseMetric {

	public CountInstancesMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		//MLIC: new
		return ontology.getIndividualsInSignature().size();
	}
	
	public String toString() {
		return "Instances count";
	    }

}
