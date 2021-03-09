package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountFunctionalDataPropertyAxiomsMetric extends DataPropertyAxiomsMetric {

    public CountFunctionalDataPropertyAxiomsMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
    	// MLIC: changed getAxiomCount with boolean is deprecated
    	// return ontology.getAxiomCount(AxiomType.FUNCTIONAL_DATA_PROPERTY, true);
    	return ontology.getAxiomCount(AxiomType.FUNCTIONAL_DATA_PROPERTY, OntologyUtility.ImportClosures(true));
    }

    @Override
    public String toString() {
	return "Functional data property axioms count";
    }

}
