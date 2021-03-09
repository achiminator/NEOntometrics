package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountEquivalentObjectPropertiesAxiomsMetric extends ObjectPropertyAxiomsMetric {

    public CountEquivalentObjectPropertiesAxiomsMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
    	// MLIC: changed getAxiomCount with boolean is deprecated
    	//return ontology.getAxiomCount(AxiomType.EQUIVALENT_OBJECT_PROPERTIES,true);
    	return ontology.getAxiomCount(AxiomType.EQUIVALENT_OBJECT_PROPERTIES,OntologyUtility.ImportClosures(true));
    }

    @Override
    public String toString() {
	return "Equivalent object properties axioms count";
    }

}
