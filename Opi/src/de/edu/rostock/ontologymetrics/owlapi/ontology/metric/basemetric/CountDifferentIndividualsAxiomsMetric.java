package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountDifferentIndividualsAxiomsMetric extends IndividualAxiomsMetric {

	public CountDifferentIndividualsAxiomsMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		//MLIC: changed getAxiomCount with boolean is deprecated
		//return ontology.getAxiomCount(AxiomType.DIFFERENT_INDIVIDUALS, true);
		return ontology.getAxiomCount(AxiomType.DIFFERENT_INDIVIDUALS, OntologyUtility.ImportClosures(true));
	}

	@Override
	public String toString() {
		return "Different individuals axioms count";
	}

}
