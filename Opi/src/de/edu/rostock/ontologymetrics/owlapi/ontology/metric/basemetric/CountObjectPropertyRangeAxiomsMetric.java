package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountObjectPropertyRangeAxiomsMetric extends ObjectPropertyAxiomsMetric {

	public CountObjectPropertyRangeAxiomsMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// MLIC: changed getAxiomCount with boolean is deprecated
		// return ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_RANGE, true);
		return ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_RANGE, OntologyUtility.ImportClosures(true));
	}

	@Override
	public String toString() {
		return "Object property range axioms count";
	}

}
