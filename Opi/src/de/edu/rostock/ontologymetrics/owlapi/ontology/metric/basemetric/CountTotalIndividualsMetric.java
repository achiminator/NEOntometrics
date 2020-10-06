package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountTotalIndividualsMetric extends BaseMetric {

	public CountTotalIndividualsMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// MLIC: changed function with boolean is deprecated
		// return ontology.getIndividualsInSignature(true).size();
		return ontology.getIndividualsInSignature(OntologyUtility.ImportClosures(true)).size();
	}

	@Override
	public String toString() {
		return "Total individuals count";
	}

}
