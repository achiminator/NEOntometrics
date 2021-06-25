package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class ObjectPropertyAxiomsMetric {
    private int subObjectPropertyOfAxiomsCount;
    private int equivalentObjectPropertyAxiomsCount;
    private int inverseObjectPropertyAxiomsCount;
    private int disjointObjectPropertyAxiomsCount;
    private int functionalObjectPropertiyAxiomsCount;
    private int inverseFunctionalObjectPropertyAxiomsCount;
    private int transitiveObjectPropertyAxiomsCount;
    private int symmetricObjectPropertyAxiomsCount;
    private int asymmetricObjectPropertyAxiomsCount;
    private int reflexiveObjectPropertyAxiomsCount;
    private int irreflexiveObjectPropertyAxiomsCount;
    private int objectPropertyDomainAxiomsCount;
    private int objectPropertyRangeAxiomsCount;
    private int subPropertyChainOfAxiomsCount;

    public int getSubObjectPropertyOfAxiomsCount() {
	return subObjectPropertyOfAxiomsCount;
    }

    public int getEquivalentObjectPropertyAxiomsCount() {
	return equivalentObjectPropertyAxiomsCount;
    }

    public int getInverseObjectPropertyAxiomsCount() {
	return inverseObjectPropertyAxiomsCount;
    }

    public int getDisjointObjectPropertyAxiomsCount() {
	return disjointObjectPropertyAxiomsCount;
    }

    public int getFunctionalObjectPropertiyAxiomsCount() {
	return functionalObjectPropertiyAxiomsCount;
    }

    public int getInverseFunctionalObjectPropertyAxiomsCount() {
	return inverseFunctionalObjectPropertyAxiomsCount;
    }

    public int getTransitiveObjectPropertyAxiomsCount() {
	return transitiveObjectPropertyAxiomsCount;
    }

    public int getSymmetricObjectPropertyAxiomsCount() {
	return symmetricObjectPropertyAxiomsCount;
    }

    public int getAsymmetricObjectPropertyAxiomsCount() {
	return asymmetricObjectPropertyAxiomsCount;
    }

    public int getReflexiveObjectPropertyAxiomsCount() {
	return reflexiveObjectPropertyAxiomsCount;
    }

    public int getIrreflexiveObjectPropertyAxiomsCount() {
	return irreflexiveObjectPropertyAxiomsCount;
    }

    public int getObjectPropertyDomainAxiomsCount() {
	return objectPropertyDomainAxiomsCount;
    }

    public int getObjectPropertyRangeAxiomsCount() {
	return objectPropertyRangeAxiomsCount;
    }

    public int getSubPropertyChainOfAxiomsCount() {
	return subPropertyChainOfAxiomsCount;
    }

    public Map<String, Object> getAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("SubObjectPropertyOfaxiomscount", subObjectPropertyOfAxiomsCount);
	returnObject.put("Equivalentobjectpropertiesaxiomscount", equivalentObjectPropertyAxiomsCount);
	returnObject.put("Inverseobjectpropertiesaxiomscount", inverseObjectPropertyAxiomsCount);
	returnObject.put("Disjointobjectpropertiesaxiomscount", disjointObjectPropertyAxiomsCount);
	returnObject.put("Functionalobjectpropertiesaxiomscount", functionalObjectPropertiyAxiomsCount);
	returnObject.put("Inversefunctionalobjectpropertiesaxiomscount", inverseFunctionalObjectPropertyAxiomsCount);
	returnObject.put("Transitiveobjectpropertyaxiomscount", transitiveObjectPropertyAxiomsCount);
	returnObject.put("Symmetricobjectpropertyaxiomscount", symmetricObjectPropertyAxiomsCount);
	returnObject.put("Asymmetricobjectpropertyaxiomscount", asymmetricObjectPropertyAxiomsCount);
	returnObject.put("Reflexiveobjectpropertyaxiomscount", reflexiveObjectPropertyAxiomsCount);
	returnObject.put("Irreflexiveobjectpropertyaxiomscount", irreflexiveObjectPropertyAxiomsCount);
	returnObject.put("Objectpropertydomainaxiomscount", objectPropertyDomainAxiomsCount);
	returnObject.put("Objectpropertyrangeaxiomscount", objectPropertyRangeAxiomsCount);
	returnObject.put("SubPropertyChainOfaxiomscount", subPropertyChainOfAxiomsCount);

	return returnObject;
    }

    public Map<String, Object> calculateAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("SubObjectPropertyOfaxiomscount", countSubObjectPropertyAxioms(ontology));
	returnObject.put("Equivalentobjectpropertiesaxiomscount", countEquivalentObjectPropertyAxioms(ontology));
	returnObject.put("Inverseobjectpropertiesaxiomscount", countInverseObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Disjointobjectpropertiesaxiomscount", countDisjointObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Functionalobjectpropertiesaxiomscount", countFunctionalObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Inversefunctionalobjectpropertiesaxiomscount",
		countInverseFunctionalObjectPropertiesAxiomsMetric(ontology));
	returnObject.put("Transitiveobjectpropertyaxiomscount", countTransitiveObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Symmetricobjectpropertyaxiomscount", countSymmetricObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Asymmetricobjectpropertyaxiomscount",
		countAsymmetricObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Reflexiveobjectpropertyaxiomscount", countReflexiveObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Irreflexiveobjectpropertyaxiomscount",
		countIrreflexiveObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Objectpropertydomainaxiomscount", countObjectPropertyDomainAxiomsMetric(ontology));
	returnObject.put("Objectpropertyrangeaxiomscount", countObjectPropertyRangeAxiomsMetric(ontology));
	returnObject.put("SubPropertyChainOfaxiomscount", countSubPropertyChainOfAxiomsMetric(ontology));

	return returnObject;
    }

    public int countSubObjectPropertyAxioms(OWLOntology ontology) {
	subObjectPropertyOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(true));
	return subObjectPropertyOfAxiomsCount;
    }

    public int countEquivalentObjectPropertyAxioms(OWLOntology ontology) {
	equivalentObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(true));
	return equivalentObjectPropertyAxiomsCount;
    }

    public int countInverseObjectPropertyAxiomsMetric(OWLOntology ontology) {
	inverseObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.INVERSE_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(true));
	return inverseObjectPropertyAxiomsCount;
    }

    public int countDisjointObjectPropertyAxiomsMetric(OWLOntology ontology) {
	disjointObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(true));
	return disjointObjectPropertyAxiomsCount;
    }

    public int countFunctionalObjectPropertyAxiomsMetric(OWLOntology ontology) {
	functionalObjectPropertiyAxiomsCount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(true));
	return functionalObjectPropertiyAxiomsCount;
    }

    public int countInverseFunctionalObjectPropertiesAxiomsMetric(OWLOntology ontology) {
	inverseFunctionalObjectPropertyAxiomsCount = ontology
		.getAxiomCount(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, OntologyUtility.ImportClosures(true));
	return inverseFunctionalObjectPropertyAxiomsCount;
    }

    public int countTransitiveObjectPropertyAxiomsMetric(OWLOntology ontology) {
	transitiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.TRANSITIVE_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(true));
	return transitiveObjectPropertyAxiomsCount;
    }

    public int countSymmetricObjectPropertyAxiomsMetric(OWLOntology ontology) {
	symmetricObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.SYMMETRIC_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(true));
	return symmetricObjectPropertyAxiomsCount;
    }

    public int countAsymmetricObjectPropertyAxiomsMetric(OWLOntology ontology) {
	asymmetricObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.ASYMMETRIC_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(true));
	return asymmetricObjectPropertyAxiomsCount;
    }

    public int countReflexiveObjectPropertyAxiomsMetric(OWLOntology ontology) {
	reflexiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.REFLEXIVE_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(true));
	return reflexiveObjectPropertyAxiomsCount;
    }

    public int countIrreflexiveObjectPropertyAxiomsMetric(OWLOntology ontology) {
	irreflexiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(true));
	return irreflexiveObjectPropertyAxiomsCount;
    }

    public int countObjectPropertyDomainAxiomsMetric(OWLOntology ontology) {
	objectPropertyDomainAxiomsCount = ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_DOMAIN,
		OntologyUtility.ImportClosures(true));
	return objectPropertyDomainAxiomsCount;
    }

    public int countObjectPropertyRangeAxiomsMetric(OWLOntology ontology) {
	objectPropertyRangeAxiomsCount = ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_RANGE,
		OntologyUtility.ImportClosures(true));
	return objectPropertyRangeAxiomsCount;
    }

    public int countSubPropertyChainOfAxiomsMetric(OWLOntology ontology) {
	subPropertyChainOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_PROPERTY_CHAIN_OF,
		OntologyUtility.ImportClosures(true));
	return subPropertyChainOfAxiomsCount;
    }
}
