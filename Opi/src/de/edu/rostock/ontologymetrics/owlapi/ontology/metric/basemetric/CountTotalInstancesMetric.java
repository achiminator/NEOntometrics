package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountTotalInstancesMetric extends BaseMetric {

	public CountTotalInstancesMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// MLIC: changed function with boolean is deprecated
		// return ontology.getIndividualsInSignature(true).size();
		return ontology.getIndividualsInSignature(OntologyUtility.ImportClosures(true)).size();
	}

	public String toString() {
		return "Total instances count";
	}
}
