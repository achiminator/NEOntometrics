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

	returnObject.put("objectPropertiesOnClasses", classRelations);
	returnObject.put("classesWithObjectProperties", classesWithRelations);
    }

    private void countSubObjectPropertyAxioms() {
	int subObjectPropertyOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("subObjectPropertyOfAxioms", subObjectPropertyOfAxiomsCount);

    }

    private void countEquivalentObjectPropertyAxioms() {
	int equivalentObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("equivalentObjectPropertyAxioms", equivalentObjectPropertyAxiomsCount);
    }

    private void countInverseObjectPropertyAxiomsMetric() {
	int inverseObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.INVERSE_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("inverseObjectPropertyAxioms", inverseObjectPropertyAxiomsCount);
    }

    private void countDisjointObjectPropertyAxiomsMetric() {
	int disjointObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("disjointObjectPropertyAxioms", disjointObjectPropertyAxiomsCount);
    }

    private void countFunctionalObjectPropertyAxiomsMetric() {
	int functionalObjectPropertiyAxiomsCount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("functionalObjectPropertyAxioms", functionalObjectPropertiyAxiomsCount);
    }

    private void countInverseFunctionalObjectPropertiesAxiomsMetric() {
	int inverseFunctionalObjectPropertyAxiomsCount = ontology
		.getAxiomCount(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, OntologyUtility.ImportClosures(imports));
	returnObject.put("inverseFunctionalObjectPropertyAxioms", inverseFunctionalObjectPropertyAxiomsCount);

    }

    private void countTransitiveObjectPropertyAxiomsMetric() {
	int transitiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.TRANSITIVE_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("transitiveObjectPropertyAxioms", transitiveObjectPropertyAxiomsCount);

    }

    private void countSymmetricObjectPropertyAxiomsMetric() {
	int symmetricObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.SYMMETRIC_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("symmetricObjectPropertyAxioms", symmetricObjectPropertyAxiomsCount);

    }

    private void countAsymmetricObjectPropertyAxiomsMetric() {
	int asymmetricObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.ASYMMETRIC_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("asymmetricObjectPropertyAxioms", asymmetricObjectPropertyAxiomsCount);
    }

    private void countReflexiveObjectPropertyAxiomsMetric() {
	int reflexiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.REFLEXIVE_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("reflexiveObjectPropertyAxioms", reflexiveObjectPropertyAxiomsCount);
    }

    private void countIrreflexiveObjectPropertyAxiomsMetric() {
	int irreflexiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("irreflexiveObjectPropertyAxioms", irreflexiveObjectPropertyAxiomsCount);
    }

    private void countObjectPropertyDomainAxiomsMetric() {
	int objectPropertyDomainAxiomsCount = ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_DOMAIN,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("objectPropertyDomainAxioms", objectPropertyDomainAxiomsCount);
    }

    private void countObjectPropertyRangeAxiomsMetric() {
	int objectPropertyRangeAxiomsCount = ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_RANGE,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("objectPropertyRangeAxioms", objectPropertyRangeAxiomsCount);

    }

    private void countSubPropertyChainOfAxiomsMetric() {
	int subPropertyChainOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_PROPERTY_CHAIN_OF,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("subPropertyChainOfAxioms", subPropertyChainOfAxiomsCount);
    }
}
