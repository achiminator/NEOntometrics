package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import uk.ac.manchester.cs.owl.owlapi.OWLClassAssertionAxiomImpl;

public class ObjectPropertyAxiomsMetric implements Callable<ObjectPropertyAxiomsMetric> {
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
    private OWLOntology ontology;
    private boolean withImports;
    private int classRelations;
    private int classesWithRelations;

    public ObjectPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	this.ontology = ontology;
	this.withImports = withImports;
    }

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

    public Map<String, Object> getAllMetrics() {
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
	returnObject.put("Classobjectproperties", classRelations);
	returnObject.put("Classeswithobjectproperties", classesWithRelations);
	return returnObject;
    }

    public ObjectPropertyAxiomsMetric call() {
	objectPropertyByClass(withImports);
	countSubObjectPropertyAxioms(ontology, withImports);
	countEquivalentObjectPropertyAxioms(ontology, withImports);
	countInverseObjectPropertyAxiomsMetric(ontology, withImports);
	countDisjointObjectPropertyAxiomsMetric(ontology, withImports);
	countFunctionalObjectPropertyAxiomsMetric(ontology, withImports);
	countInverseFunctionalObjectPropertiesAxiomsMetric(ontology, withImports);
	countTransitiveObjectPropertyAxiomsMetric(ontology, withImports);
	countSymmetricObjectPropertyAxiomsMetric(ontology, withImports);
	countAsymmetricObjectPropertyAxiomsMetric(ontology, withImports);
	countReflexiveObjectPropertyAxiomsMetric(ontology, withImports);
	countIrreflexiveObjectPropertyAxiomsMetric(ontology, withImports);
	countObjectPropertyDomainAxiomsMetric(ontology, withImports);
	countObjectPropertyRangeAxiomsMetric(ontology, withImports);
	countSubPropertyChainOfAxiomsMetric(ontology, withImports);

	return this;
    }
/**
 * Calculates the number of Classes with Object Properties
 * @param withImports
 */
    public void objectPropertyByClass(boolean withImports) {
	Set<OWLClass> classes = ontology.getClassesInSignature();
	for (OWLClass owlClass : classes) {
	    boolean classhasObjectProperty = false;
	    for (OWLSubClassOfAxiom classExpr : ontology.getSubClassAxiomsForSubClass(owlClass)) {
		for (OWLEntity entity : classExpr.getSignature()) {
		    if(entity.isOWLObjectProperty()) {
			classhasObjectProperty = true;
			classRelations++;
		    }
		    
		}
	    }
	    if(classhasObjectProperty)
		classesWithRelations++;
	}
    }

    public int countSubObjectPropertyAxioms(OWLOntology ontology, boolean withImports) {
	subObjectPropertyOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(withImports));
	return subObjectPropertyOfAxiomsCount;
    }

    public int countEquivalentObjectPropertyAxioms(OWLOntology ontology, boolean withImports) {
	equivalentObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(withImports));
	return equivalentObjectPropertyAxiomsCount;
    }

    public int countInverseObjectPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	inverseObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.INVERSE_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(withImports));
	return inverseObjectPropertyAxiomsCount;
    }

    public int countDisjointObjectPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	disjointObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(withImports));
	return disjointObjectPropertyAxiomsCount;
    }

    public int countFunctionalObjectPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	functionalObjectPropertiyAxiomsCount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(withImports));
	return functionalObjectPropertiyAxiomsCount;
    }

    public int countInverseFunctionalObjectPropertiesAxiomsMetric(OWLOntology ontology, boolean withImports) {
	inverseFunctionalObjectPropertyAxiomsCount = ontology.getAxiomCount(
		AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, OntologyUtility.ImportClosures(withImports));
	return inverseFunctionalObjectPropertyAxiomsCount;
    }

    public int countTransitiveObjectPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	transitiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.TRANSITIVE_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(withImports));
	return transitiveObjectPropertyAxiomsCount;
    }

    public int countSymmetricObjectPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	symmetricObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.SYMMETRIC_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(withImports));
	return symmetricObjectPropertyAxiomsCount;
    }

    public int countAsymmetricObjectPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	asymmetricObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.ASYMMETRIC_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(withImports));
	return asymmetricObjectPropertyAxiomsCount;
    }

    public int countReflexiveObjectPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	reflexiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.REFLEXIVE_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(withImports));
	return reflexiveObjectPropertyAxiomsCount;
    }

    public int countIrreflexiveObjectPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	irreflexiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(withImports));
	return irreflexiveObjectPropertyAxiomsCount;
    }

    public int countObjectPropertyDomainAxiomsMetric(OWLOntology ontology, boolean withImports) {
	objectPropertyDomainAxiomsCount = ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_DOMAIN,
		OntologyUtility.ImportClosures(withImports));
	return objectPropertyDomainAxiomsCount;
    }

    public int countObjectPropertyRangeAxiomsMetric(OWLOntology ontology, boolean withImports) {
	objectPropertyRangeAxiomsCount = ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_RANGE,
		OntologyUtility.ImportClosures(withImports));
	return objectPropertyRangeAxiomsCount;
    }

    public int countSubPropertyChainOfAxiomsMetric(OWLOntology ontology, boolean withImports) {
	subPropertyChainOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_PROPERTY_CHAIN_OF,
		OntologyUtility.ImportClosures(withImports));
	return subPropertyChainOfAxiomsCount;
    }
}
