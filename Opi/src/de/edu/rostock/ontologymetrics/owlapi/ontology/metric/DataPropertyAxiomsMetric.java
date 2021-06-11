package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class DataPropertyAxiomsMetric {

    public Map<String, Object> getAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("SubDataPropertyOf axioms count", countSubDataPropertyOfAxiomsMetric(ontology));
	returnObject.put("Equivalent data properties axioms count", countEquivalentDataPropertyAxiomsMetric(ontology));
	returnObject.put("Disjoint data properties axioms count", countDisjointDataPropertyAxiomsMetric(ontology));
	returnObject.put("Functional data property axioms count", countFunctionalDataPropertyAxiomsMetric(ontology));
	returnObject.put("Data property domain axioms count", countDataPropertyDomainAxiomsMetric(ontology));
	returnObject.put("Data Property range axioms count", countDataPropertyRangeAxiomsMetric(ontology));
	return returnObject;
    }

    public int countSubDataPropertyOfAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.SUB_DATA_PROPERTY, OntologyUtility.ImportClosures(true));
    }

    public int countEquivalentDataPropertyAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.EQUIVALENT_DATA_PROPERTIES, OntologyUtility.ImportClosures(true));
    }

    public int countDisjointDataPropertyAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.DISJOINT_DATA_PROPERTIES, OntologyUtility.ImportClosures(true));
    }

    public int countFunctionalDataPropertyAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.FUNCTIONAL_DATA_PROPERTY, OntologyUtility.ImportClosures(true));
    }

    public int countDataPropertyDomainAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.DATA_PROPERTY_DOMAIN, OntologyUtility.ImportClosures(true));
    }
    public int countDataPropertyRangeAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.DATA_PROPERTY_RANGE, OntologyUtility.ImportClosures(true));
    }
}
