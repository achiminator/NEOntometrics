package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountTotalObjectPropertiesMetric extends BaseMetric {

	public CountTotalObjectPropertiesMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// the owl:TopObjectProperty inclusive
		// MLIC: changed function with boolean is deprecated
		// return ontology.getObjectPropertiesInSignature(true).size();
		return ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();
	}

	@Override
	public String toString() {
		return "Total object properties count";
	}

}
