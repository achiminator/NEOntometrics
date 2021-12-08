package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.Set;
import java.util.concurrent.Callable;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class ObjectPropertyAxiomsMetric extends MetricCalculations implements Callable<ObjectPropertyAxiomsMetric> {

    public ObjectPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	super(ontology, withImports);
    }

    public ObjectPropertyAxiomsMetric call() {
	objectPropertyByClass();
	countSubObjectPropertyAxioms();
	countEquivalentObjectPropertyAxioms();
	countInverseObjectPropertyAxiomsMetric();
	countDisjointObjectPropertyAxiomsMetric();
	countFunctionalObjectPropertyAxiomsMetric();
	countInverseFunctionalObjectPropertiesAxiomsMetric();
	countTransitiveObjectPropertyAxiomsMetric();
	countSymmetricObjectPropertyAxiomsMetric();
	countAsymmetricObjectPropertyAxiomsMetric();
	countReflexiveObjectPropertyAxiomsMetric();
	countIrreflexiveObjectPropertyAxiomsMetric();
	countObjectPropertyDomainAxiomsMetric();
	countObjectPropertyRangeAxiomsMetric();
	countSubPropertyChainOfAxiomsMetric();

	return this;
    }

    /**
     * Calculates the number of Classes with Object Properties
     * 
     * @param withImports
     */
    public void objectPropertyByClass() {
	Set<OWLClass> classes = ontology.getClassesInSignature(OntologyUtility.ImportClosures(imports));
	int classRelations = 0;
	int classesWithRelations = 0;
	for (OWLClass owlClass : classes) {
	    boolean classhasObjectProperty = false;
	    for (OWLSubClassOfAxiom classExpr : ontology.getSubClassAxiomsForSubClass(owlClass)) {
		for (OWLEntity entity : classExpr.getSignature()) {
		    if (entity.isOWLObjectProperty()) {
			classhasObjectProperty = true;
			classRelations++;
		    }

		}
	    }
	    if (classhasObjectProperty)
		classesWithRelations++;
	}

	returnObject.put("Classobjectproperties", classRelations);
	returnObject.put("Classeswithobjectproperties", classesWithRelations);
    }

    public void countSubObjectPropertyAxioms() {
	int subObjectPropertyOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("SubObjectPropertyOfaxiomscount", subObjectPropertyOfAxiomsCount);

    }

    public void countEquivalentObjectPropertyAxioms() {
	int equivalentObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Equivalentobjectpropertiesaxiomscount", equivalentObjectPropertyAxiomsCount);
    }

    public void countInverseObjectPropertyAxiomsMetric() {
	int inverseObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.INVERSE_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Inverseobjectpropertiesaxiomscount", inverseObjectPropertyAxiomsCount);
    }

    public void countDisjointObjectPropertyAxiomsMetric() {
	int disjointObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Disjointobjectpropertiesaxiomscount", disjointObjectPropertyAxiomsCount);
    }

    public void countFunctionalObjectPropertyAxiomsMetric() {
	int functionalObjectPropertiyAxiomsCount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Functionalobjectpropertiesaxiomscount", functionalObjectPropertiyAxiomsCount);
    }

    public void countInverseFunctionalObjectPropertiesAxiomsMetric() {
	int inverseFunctionalObjectPropertyAxiomsCount = ontology
		.getAxiomCount(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, OntologyUtility.ImportClosures(imports));
	returnObject.put("Inversefunctionalobjectpropertiesaxiomscount", inverseFunctionalObjectPropertyAxiomsCount);

    }

    public void countTransitiveObjectPropertyAxiomsMetric() {
	int transitiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.TRANSITIVE_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Transitiveobjectpropertyaxiomscount", transitiveObjectPropertyAxiomsCount);

    }

    public void countSymmetricObjectPropertyAxiomsMetric() {
	int symmetricObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.SYMMETRIC_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Symmetricobjectpropertyaxiomscount", symmetricObjectPropertyAxiomsCount);

    }

    public void countAsymmetricObjectPropertyAxiomsMetric() {
	int asymmetricObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.ASYMMETRIC_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Asymmetricobjectpropertyaxiomscount", asymmetricObjectPropertyAxiomsCount);
    }

    public void countReflexiveObjectPropertyAxiomsMetric() {
	int reflexiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.REFLEXIVE_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Reflexiveobjectpropertyaxiomscount", reflexiveObjectPropertyAxiomsCount);
    }

    public void countIrreflexiveObjectPropertyAxiomsMetric() {
	int irreflexiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Irreflexiveobjectpropertyaxiomscount", irreflexiveObjectPropertyAxiomsCount);
    }

    public void countObjectPropertyDomainAxiomsMetric() {
	int objectPropertyDomainAxiomsCount = ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_DOMAIN,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Objectpropertydomainaxiomscount", objectPropertyDomainAxiomsCount);
    }

    public void countObjectPropertyRangeAxiomsMetric() {
	int objectPropertyRangeAxiomsCount = ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_RANGE,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Objectpropertyrangeaxiomscount", objectPropertyRangeAxiomsCount);

    }

    public void countSubPropertyChainOfAxiomsMetric() {
	int subPropertyChainOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_PROPERTY_CHAIN_OF,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("SubPropertyChainOfaxiomscount", subPropertyChainOfAxiomsCount);
    }
}
