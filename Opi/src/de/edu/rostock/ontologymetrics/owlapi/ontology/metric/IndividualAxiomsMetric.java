package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.concurrent.Callable;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class IndividualAxiomsMetric extends MetricCalculations implements Callable<IndividualAxiomsMetric> {

    public IndividualAxiomsMetric(OWLOntology ontology, boolean withImports) {
	super(ontology, withImports);
    }

    public IndividualAxiomsMetric call() {

	countClassAssertionAxiomsMetric();
	countObjectPropertyAssertionAxiomsMetric();
	countDataPropertyAssertionAxiomsMetric();
	countNegativeObjectPropertyAssertionAxiomsMetric();
	countNegativeDataPropertyAssertionAxiomsMetric();
	countSameIndividualsAxiomsMetric();
	countDifferentIndividualsAxiomsMetric();

	return this;
    }

    public void countClassAssertionAxiomsMetric() {
	int classAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.CLASS_ASSERTION,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("classAssertionaxioms", classAssertionAxiomsCount);

    }

    public void countObjectPropertyAssertionAxiomsMetric() {
	int objectPropertyAssertionAxiomsCount = ontology
		.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(imports)).size();
	returnObject.put("objectPropertyAssertionaxioms", objectPropertyAssertionAxiomsCount);

    }

    public void countDataPropertyAssertionAxiomsMetric() {
	int dataPropertyAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_ASSERTION,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("dataPropertyAssertionAxioms", dataPropertyAssertionAxiomsCount);

    }

    public void countNegativeObjectPropertyAssertionAxiomsMetric() {
	int negativeObjectPropertyAssertionAxiomsCount = ontology
		.getAxiomCount(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(imports));
	returnObject.put("negativeObjectPropertyAssertionAxioms", negativeObjectPropertyAssertionAxiomsCount);

    }

    public void countNegativeDataPropertyAssertionAxiomsMetric() {
	int negativeDataPropertyAssertionAxiomsCount = ontology
		.getAxiomCount(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(imports));
	returnObject.put("negativeDataPropertyAssertionAxioms", negativeDataPropertyAssertionAxiomsCount);

    }

    public void countSameIndividualsAxiomsMetric() {
	int sameIndividualAxiomsCount = ontology.getAxiomCount(AxiomType.SAME_INDIVIDUAL,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("sameIndividualsAxioms", sameIndividualAxiomsCount);

    }

    public void countDifferentIndividualsAxiomsMetric() {
	int differentIndividualAxiomsCount = ontology.getAxiomCount(AxiomType.DIFFERENT_INDIVIDUALS,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("differentIndividualsAxioms", differentIndividualAxiomsCount);
    }
}
