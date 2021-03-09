package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountSameIndividualsAxiomsMetric extends IndividualAxiomsMetric {

	public CountSameIndividualsAxiomsMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// MLIC: changed getAxiomCount with boolean is deprecated
		// return ontology.getAxiomCount(AxiomType.SAME_INDIVIDUAL, true);
		return ontology.getAxiomCount(AxiomType.SAME_INDIVIDUAL, OntologyUtility.ImportClosures(true));
	}

	@Override
	public String toString() {
		return "Same individuals axioms count";
	}

}
