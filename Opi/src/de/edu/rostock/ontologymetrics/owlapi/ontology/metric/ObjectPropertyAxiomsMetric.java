package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;


public abstract class ObjectPropertyAxiomsMetric {
    public Map<String, Object> getAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("SubObjectPropertyOf axioms count", countSubObjectPropertyAxioms(ontology));
	returnObject.put("Equivalent object properties axioms count", countEquivalentObjectPropertyAxioms(ontology));
	returnObject.put("Inverse object properties axioms count", countInverseObjectPropertyAxiomsMetri(ontology));
	returnObject.put("Disjoint object properties axioms count", countDisjointObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Functional object properties axioms count", countFunctionalObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Inverse functional object properties axioms count", countInverseFunctionalObjectPropertiesAxiomsMetric(ontology));
	returnObject.put("Transitive object property axioms count", countTransitiveObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Symmetric object property axioms count", countSymmetricObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Asymmetric object property axioms count", countAsymmetricObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Reflexive object property axioms count", countReflexiveObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Irreflexive object property axioms count", countIrreflexiveObjectPropertyAxiomsMetric(ontology));
	returnObject.put("Object property domain axioms count", countObjectPropertyDomainAxiomsMetric(ontology));
	returnObject.put("Object property range axioms count", countObjectPropertyRangeAxiomsMetric(ontology));
	returnObject.put("SubPropertyChainOf axioms count", countSubPropertyChainOfAxiomsMetric(ontology));
	
	return returnObject;
    }

    public int countSubObjectPropertyAxioms(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.SUB_OBJECT_PROPERTY, OntologyUtility.ImportClosures(true));
    }
    public int countEquivalentObjectPropertyAxioms(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.EQUIVALENT_OBJECT_PROPERTIES,OntologyUtility.ImportClosures(true));
    }
    public int countInverseObjectPropertyAxiomsMetri(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.INVERSE_OBJECT_PROPERTIES, OntologyUtility.ImportClosures(true));
    }
    public int countDisjointObjectPropertyAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.DISJOINT_OBJECT_PROPERTIES,OntologyUtility.ImportClosures(true));
    }
    public int countFunctionalObjectPropertyAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.FUNCTIONAL_OBJECT_PROPERTY, OntologyUtility.ImportClosures(true));
    }
    public int countInverseFunctionalObjectPropertiesAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, OntologyUtility.ImportClosures(true));
    }
    public int countTransitiveObjectPropertyAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.TRANSITIVE_OBJECT_PROPERTY, OntologyUtility.ImportClosures(true));
    }
    public int countSymmetricObjectPropertyAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.SYMMETRIC_OBJECT_PROPERTY, OntologyUtility.ImportClosures(true));
    }
    public int countAsymmetricObjectPropertyAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.ASYMMETRIC_OBJECT_PROPERTY,OntologyUtility.ImportClosures(true));
    }
    public int countReflexiveObjectPropertyAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.REFLEXIVE_OBJECT_PROPERTY, OntologyUtility.ImportClosures(true));
    }
    public int countIrreflexiveObjectPropertyAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY, OntologyUtility.ImportClosures(true));
    }
    public int countObjectPropertyDomainAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_DOMAIN, OntologyUtility.ImportClosures(true));
    }
    public int countObjectPropertyRangeAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_RANGE, OntologyUtility.ImportClosures(true));
    }
    public int countSubPropertyChainOfAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.SUB_PROPERTY_CHAIN_OF, OntologyUtility.ImportClosures(true));
    }
}
