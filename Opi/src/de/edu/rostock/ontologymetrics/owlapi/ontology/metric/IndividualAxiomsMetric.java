package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;


public abstract class IndividualAxiomsMetric  {


    public Map<String, Object> getAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("Class assertion axioms count", countClassAssertionAxiomsMetric(ontology));
	returnObject.put("Object property assertion axioms count", countObjectPropertyAssertionAxiomsMetric(ontology));
	returnObject.put("Data property assertion axioms count", countDataPropertyAssertionAxiomsMetric(ontology));
	returnObject.put("Negative object property assertion axioms count", countNegativeObjectPropertyAssertionAxiomsMetric(ontology));
	returnObject.put("Negative data property assertion axioms count", countNegativeDataPropertyAssertionAxiomsMetric(ontology));
	returnObject.put("Same individuals axioms count", countSameIndividualsAxiomsMetric(ontology));
	returnObject.put("Different individuals axioms count", countDifferentIndividualsAxiomsMetric(ontology));
	
	return returnObject;
    }
    public int countClassAssertionAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.CLASS_ASSERTION, OntologyUtility.ImportClosures(true));
    }
    public int countObjectPropertyAssertionAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(true)).size();
    }
    public int countDataPropertyAssertionAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.DATA_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(true));
    }
    public int countNegativeObjectPropertyAssertionAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(true));
    }
    public int countNegativeDataPropertyAssertionAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(true));
    }
    public int countSameIndividualsAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.SAME_INDIVIDUAL, OntologyUtility.ImportClosures(true));
	
    }
    public int countDifferentIndividualsAxiomsMetric (OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.DIFFERENT_INDIVIDUALS, OntologyUtility.ImportClosures(true));
    }
}
