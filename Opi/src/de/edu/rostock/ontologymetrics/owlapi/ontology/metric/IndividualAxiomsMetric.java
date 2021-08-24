package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class IndividualAxiomsMetric implements Callable<IndividualAxiomsMetric> {
    private int classAssertionAxiomsCount;
    private int objectPropertyAssertionAxiomsCount;
    private int dataPropertyAssertionAxiomsCount;
    private int negativeObjectPropertyAssertionAxiomsCount;
    private int negativeDataPropertyAssertionAxiomsCount;
    private int sameIndividualAxiomsCount;
    private int differentIndividualAxiomsCount;
    private OWLOntology ontology;
    private boolean withImports;

    public IndividualAxiomsMetric(OWLOntology ontology, boolean withImports) {
	this.ontology = ontology;
	this.withImports = withImports;
    }

    public Map<String, Object> getAllMetrics() {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("Classassertionaxiomscount", classAssertionAxiomsCount);
	returnObject.put("Objectpropertyassertionaxiomscount", objectPropertyAssertionAxiomsCount);
	returnObject.put("Datapropertyassertionaxiomscount", dataPropertyAssertionAxiomsCount);
	returnObject.put("Negativeobjectpropertyassertionaxiomscount", negativeObjectPropertyAssertionAxiomsCount);
	returnObject.put("Negativedatapropertyassertionaxiomscount", negativeDataPropertyAssertionAxiomsCount);
	returnObject.put("Sameindividualsaxiomscount", sameIndividualAxiomsCount);
	returnObject.put("Differentindividualsaxiomscount", differentIndividualAxiomsCount);

	return returnObject;
    }

    public IndividualAxiomsMetric call() {

	countClassAssertionAxiomsMetric(ontology,withImports);
	countObjectPropertyAssertionAxiomsMetric(ontology,withImports);
	countDataPropertyAssertionAxiomsMetric(ontology,withImports);
	countNegativeObjectPropertyAssertionAxiomsMetric(ontology,withImports);
	countNegativeDataPropertyAssertionAxiomsMetric(ontology,withImports);
	countSameIndividualsAxiomsMetric(ontology,withImports);
	countDifferentIndividualsAxiomsMetric(ontology,withImports);

	return this;
    }

    public int countClassAssertionAxiomsMetric(OWLOntology ontology, boolean withImports) {
	classAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.CLASS_ASSERTION,
		OntologyUtility.ImportClosures(withImports));
	return classAssertionAxiomsCount;
    }

    public int countObjectPropertyAssertionAxiomsMetric(OWLOntology ontology, boolean withImports) {
	objectPropertyAssertionAxiomsCount = ontology
		.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(withImports)).size();
	return objectPropertyAssertionAxiomsCount;
    }

    public int countDataPropertyAssertionAxiomsMetric(OWLOntology ontology, boolean withImports) {
	dataPropertyAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_ASSERTION,
		OntologyUtility.ImportClosures(withImports));
	return dataPropertyAssertionAxiomsCount;
    }

    public int countNegativeObjectPropertyAssertionAxiomsMetric(OWLOntology ontology, boolean withImports) {
	negativeObjectPropertyAssertionAxiomsCount = ontology
		.getAxiomCount(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(withImports));
	return negativeObjectPropertyAssertionAxiomsCount;
    }

    public int countNegativeDataPropertyAssertionAxiomsMetric(OWLOntology ontology, boolean withImports) {
	negativeDataPropertyAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION,
		OntologyUtility.ImportClosures(withImports));
	return negativeDataPropertyAssertionAxiomsCount;
    }

    public int countSameIndividualsAxiomsMetric(OWLOntology ontolog, boolean withImportsy) {
	sameIndividualAxiomsCount = ontology.getAxiomCount(AxiomType.SAME_INDIVIDUAL,
		OntologyUtility.ImportClosures(withImports));
	return sameIndividualAxiomsCount;

    }

    public int countDifferentIndividualsAxiomsMetric(OWLOntology ontology, boolean withImports) {
	differentIndividualAxiomsCount = ontology.getAxiomCount(AxiomType.DIFFERENT_INDIVIDUALS,
		OntologyUtility.ImportClosures(withImports));
	return differentIndividualAxiomsCount;
    }
}
