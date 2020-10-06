package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountInverseObjectPropertiesAxiomsMetric extends ObjectPropertyAxiomsMetric {

	public CountInverseObjectPropertiesAxiomsMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// MLIC: changed getAxiomCount with boolean is deprecated
		// return ontology.getAxiomCount(AxiomType.INVERSE_OBJECT_PROPERTIES, true);
		return ontology.getAxiomCount(AxiomType.INVERSE_OBJECT_PROPERTIES, OntologyUtility.ImportClosures(true));
	}

	@Override
	public String toString() {
		return "Inverse object properties axioms count";
	}

}
