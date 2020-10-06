package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountIrreflexiveObjectPropertyAxiomsMetric extends ObjectPropertyAxiomsMetric {

	public CountIrreflexiveObjectPropertyAxiomsMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// MLIC: changed getAxiomCount with boolean is deprecated
		// return ontology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY, true);
		return ontology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY, OntologyUtility.ImportClosures(true));
	}

	@Override
	public String toString() {
		return "Irreflexive object property axioms count";
	}

}
