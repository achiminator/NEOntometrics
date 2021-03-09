package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountDataPropertyAssertionAxiomsMetric extends IndividualAxiomsMetric {

    public CountDataPropertyAssertionAxiomsMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
    	//MLIC: changed getAxiomCount with boolean is deprecated
    	//return ontology.getAxiomCount(AxiomType.DATA_PROPERTY_ASSERTION, true);
    	return ontology.getAxiomCount(AxiomType.DATA_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(true));
    }

    @Override
    public String toString() {
	return "Data property assertion axioms count";
    }

}
