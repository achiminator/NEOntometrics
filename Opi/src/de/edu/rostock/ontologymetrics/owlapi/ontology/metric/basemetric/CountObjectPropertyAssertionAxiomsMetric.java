package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountObjectPropertyAssertionAxiomsMetric extends IndividualAxiomsMetric {

	Logger myLogger = Logger.getLogger(this.getClass());
	
	public CountObjectPropertyAssertionAxiomsMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		// MLIC: getAxioms with boolean is deprecated
		//return ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION, true).size();
		return ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(true)).size();
	}

	@Override
	public String toString() {
		return "Object property assertion axioms count";
	}

}
