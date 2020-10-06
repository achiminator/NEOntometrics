package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountDisjointDataPropertiesAxiomsMetric extends DataPropertyAxiomsMetric {

	public CountDisjointDataPropertiesAxiomsMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// MLIC: changed getAxiomCount with boolean is deprecated
		// return ontology.getAxiomCount(AxiomType.DISJOINT_DATA_PROPERTIES,true);
		return ontology.getAxiomCount(AxiomType.DISJOINT_DATA_PROPERTIES, OntologyUtility.ImportClosures(true));
	}

	@Override
	public String toString() {
		return "Disjoint data properties axioms count";
	}

}
