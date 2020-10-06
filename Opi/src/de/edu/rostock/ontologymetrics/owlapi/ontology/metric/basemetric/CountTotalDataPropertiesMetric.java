package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountTotalDataPropertiesMetric extends BaseMetric {

	public CountTotalDataPropertiesMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// the owl:TopDataProperty inclusive
		// MLIC: changed function with boolean is deprecated
		// return ontology.getDataPropertiesInSignature(true).size();
		return ontology.getDataPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();
	}

	@Override
	public String toString() {
		return "Total data properties count";
	}

}
