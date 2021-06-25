package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;


public class IndividualAxiomsMetric  {
    private int classAssertionAxiomsCount;
    private int objectPropertyAssertionAxiomsCount;
    private int dataPropertyAssertionAxiomsCount;
    private int negativeObjectPropertyAssertionAxiomsCount;
    private int negativeDataPropertyAssertionAxiomsCount;
    private int sameIndividualAxiomsCount;
    private int differentIndividualAxiomsCount;

    public Map<String, Object> getAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("Classassertionaxiomscount", classAssertionAxiomsCount);
	returnObject.put("Objectpropertyassertionaxiomscount", objectPropertyAssertionAxiomsCount);
	returnObject.put("Datapropertyassertionaxiomscount", dataPropertyAssertionAxiomsCount);
	returnObject.put("Negativeobjectpropertyassertionaxiomscount", negativeObjectPropertyAssertionAxiomsCount);
	returnObject.put("Negativedatapropertyassertionaxiomscount", negativeDataPropertyAssertionAxiomsCount);
	returnObject.put("Sameindividualsaxiomscount", sameIndividualAxiomsCount);
	returnObject.put("Differentindividualsaxiomscount", differentIndividualAxiomsCount);
	
	return returnObject;
    }
    public Map<String, Object> calculateAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("Classassertionaxiomscount", countClassAssertionAxiomsMetric(ontology));
	returnObject.put("Objectpropertyassertionaxiomscount", countObjectPropertyAssertionAxiomsMetric(ontology));
	returnObject.put("Datapropertyassertionaxiomscount", countDataPropertyAssertionAxiomsMetric(ontology));
	returnObject.put("Negativeobjectpropertyassertionaxiomscount", countNegativeObjectPropertyAssertionAxiomsMetric(ontology));
	returnObject.put("Negativedatapropertyassertionaxiomscount", countNegativeDataPropertyAssertionAxiomsMetric(ontology));
	returnObject.put("Sameindividualsaxiomscount", countSameIndividualsAxiomsMetric(ontology));
	returnObject.put("Differentindividualsaxiomscount", countDifferentIndividualsAxiomsMetric(ontology));
	
	return returnObject;
    }
    public int countClassAssertionAxiomsMetric(OWLOntology ontology) {
	classAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.CLASS_ASSERTION, OntologyUtility.ImportClosures(true));
	return classAssertionAxiomsCount;
    }
    public int countObjectPropertyAssertionAxiomsMetric(OWLOntology ontology) {
	objectPropertyAssertionAxiomsCount = ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(true)).size();
	return objectPropertyAssertionAxiomsCount;
    }
    public int countDataPropertyAssertionAxiomsMetric(OWLOntology ontology) {
	dataPropertyAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(true));
	return dataPropertyAssertionAxiomsCount;
    }
    public int countNegativeObjectPropertyAssertionAxiomsMetric(OWLOntology ontology) {
	negativeObjectPropertyAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(true));
	return negativeObjectPropertyAssertionAxiomsCount;
    }
    public int countNegativeDataPropertyAssertionAxiomsMetric(OWLOntology ontology) {
	negativeDataPropertyAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(true));
	return negativeDataPropertyAssertionAxiomsCount;
    }
    public int countSameIndividualsAxiomsMetric(OWLOntology ontology) {
	sameIndividualAxiomsCount = ontology.getAxiomCount(AxiomType.SAME_INDIVIDUAL, OntologyUtility.ImportClosures(true));
	return sameIndividualAxiomsCount;
	
    }
    public int countDifferentIndividualsAxiomsMetric (OWLOntology ontology) {
	differentIndividualAxiomsCount = ontology.getAxiomCount(AxiomType.DIFFERENT_INDIVIDUALS, OntologyUtility.ImportClosures(true));
	return differentIndividualAxiomsCount;
    }
}
