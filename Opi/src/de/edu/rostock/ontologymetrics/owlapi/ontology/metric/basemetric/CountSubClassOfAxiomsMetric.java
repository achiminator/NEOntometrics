package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountSubClassOfAxiomsMetric extends ClassAxoimsMetric {

	public CountSubClassOfAxiomsMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// MLIC: changed getAxiomCount with boolean is deprecated
		// return ontology.getAxiomCount(AxiomType.SUBCLASS_OF, true);
		return ontology.getAxiomCount(AxiomType.SUBCLASS_OF, OntologyUtility.ImportClosures(true));
	}

	@Override
	public String toString() {
		return "SubClassOf axioms count";
	}

}
