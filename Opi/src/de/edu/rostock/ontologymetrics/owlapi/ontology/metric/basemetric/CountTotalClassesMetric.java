package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountTotalClassesMetric extends BaseMetric {

	Logger myLogger = Logger.getLogger(this.getClass());

	public CountTotalClassesMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// the owl:Thing inclusive
		// MLIC: getClassesInSignature with boolean is deprecated
		// return ontology.getClassesInSignature(true).size();
		return ontology.getClassesInSignature(OntologyUtility.ImportClosures(true)).size();
	}

	@Override
	public String toString() {
		return "Total classes count";
	}

}
