package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import com.google.common.collect.Multimap;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class ClassMetrics extends OntologyMetric {

    public int getClassConnectivity() {
	return classConnectivity;
    }

    public double getClassFullness() {
	return classFullness;
    }

    public double getClassImportance() {
	return classImportance;
    }

    public int getClassChildrenCount() {
	return classChildrenCount;
    }

    public double getClassInheritanceRichness() {
	return classInheritanceRichness;
    }

    public int getClassReadability() {
	return classReadability;
    }

    public int getClassRelationshipRichness() {
	return classRelationshipRichness;
    }

    public int getClassInstancesCount() {
	return classInstancesCount;
    }

    public int getClassPropertiesCount() {
	return classPropertiesCount;
    }

    private int classConnectivity;
    private double classFullness;
    private double classImportance;
    private int classChildrenCount;
    private double classInheritanceRichness;
    private int classReadability;
    private int classRelationshipRichness;
    private int classInstancesCount;
    private int classPropertiesCount;
    private IRI iri;

    private KnowledgebaseMetric knowledgebaseMetric;
    private BaseMetric baseMetric;

    public ClassMetrics(KnowledgebaseMetric knowledgebaseMetric, BaseMetric baseMetric) {
	super();
	this.knowledgebaseMetric = knowledgebaseMetric;
	this.baseMetric = baseMetric;
    }
    /**
     * Get all classMetrics for a specific IRI
     * @param ontology were the class can be found
     * @param iri
     * @return
     */
    public Map<String, Object> getAllMetrics() {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("iri", iri);
	returnObject.put("Classconnectivity", classConnectivity);
	returnObject.put("Classfulness", classFullness);
	returnObject.put("Classimportance", classImportance);
	returnObject.put("Classinheritancerichness", classInheritanceRichness);
	returnObject.put("Classreadability", classReadability);
	returnObject.put("Classchildrencount", classChildrenCount);
	returnObject.put("Classrelationshiprichness", classRelationshipRichness);
	returnObject.put("Classinstancescount", classInstancesCount);
	returnObject.put("Classpropertiescount", classPropertiesCount);

	return returnObject;
    }

    public Map<String, Object> calculateAllMetrics(OWLOntology ontology, IRI iri) {
	this.iri = iri;
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("iri", iri);
	returnObject.put("Classconnectivity", classConnectivityMetric(ontology, iri));
	returnObject.put("Classfulness", classFulnessMetric(ontology, iri));
	returnObject.put("Classimportance", classImportanceMetric(ontology, iri));
	returnObject.put("Classinheritancerichness", classInheritenceRichnessMetric(ontology, iri));
	returnObject.put("Classreadability", classReadabilityMetric(ontology, iri));
	returnObject.put("Classrelationshiprichness", classRelationshipRichnessMetric(ontology, iri));
	returnObject.put("Classchildrencount", countClassChildrenMetric(ontology, iri));
	returnObject.put("Classinstancescount", countClassInstancesMetric(ontology, iri));
	returnObject.put("Classpropertiescount", countClassPropertiesMetric(ontology, iri));

	return returnObject;
    }

    protected int countTotalInstancesOf(OWLClass cls, OWLOntology ontology) {
	int count = 0;
	if (cls != null) {
	    count = EntitySearcher.getIndividuals(cls, ontology).size();
	    for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls, ontology)) {
		count = count + countTotalInstancesOf(subClass.asOWLClass(), ontology);
	    }
	}
	return count;
    }

    public int classConnectivityMetric(OWLOntology ontology, IRI iri) {
	// TODO getConnectivity?! TEST

	int noInheritances = 0;

	// Instances of that class
	Iterator<OWLIndividual> cInstances = EntitySearcher
		.getInstances(OntologyUtility.getClass(ontology, iri), ontology).iterator();

	while (cInstances.hasNext()) {
	    // connection to others
	    Multimap<OWLObjectPropertyExpression, OWLIndividual> same = EntitySearcher
		    .getObjectPropertyValues(cInstances.next(), ontology);

	    for (OWLObjectPropertyExpression key : same.keySet()) {
		Collection<OWLIndividual> values = same.get(key);
		for (@SuppressWarnings("unused")
		OWLIndividual value : values) {
		    noInheritances++;
		}
	    }
	}
	classConnectivity = noInheritances;
	return noInheritances;
    }

    public double classFulnessMetric(OWLOntology ontology, IRI iri) {
	Integer numberOfSubClasses = countClassChildrenMetric(ontology, iri);
	Integer classInstances = countClassInstancesMetric(ontology, iri);
	double actualNumberOfInstances = 0.0;

	if (numberOfSubClasses > 0)
	    actualNumberOfInstances = ((double) classInstances / (double) numberOfSubClasses);

	double expectedNumberOfInstances = knowledgebaseMetric.getAveragePopulationMetric();

	// avoid a division by zero
	if (expectedNumberOfInstances > 0) {
	    classFullness = 0.0;
	} else {
	    classFullness = OntologyUtility.roundByGlobNK((actualNumberOfInstances / expectedNumberOfInstances));
	}
	return classFullness;

    }

    public double classImportanceMetric(OWLOntology ontology, IRI iri) {
	int countTotalInstances = baseMetric.getTotalIndividualCount();
	OWLClass cls = OntologyUtility.getClass(ontology, iri);
	int countInstances = countTotalInstancesOf(cls, ontology);

	// avoid a division by zero
	if (countTotalInstances == 0)
	    classImportance = 0.0;
	else
	    classImportance = OntologyUtility.roundByGlobNK((double) countInstances / (double) countTotalInstances);

	return classImportance;
    }

    public int countClassChildrenMetric(OWLOntology ontology, IRI iri) {
	int subClasses = 0;

	OWLClass cls = OntologyUtility.getClass(ontology, iri);

	if (cls != null) {
	    // MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    subClasses = EntitySearcher.getSubClasses(cls, ontology).size();
	    if (subClasses > 0) {
		for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls, ontology)) {
		    subClasses = subClasses + countClassChildrenMetric(ontology, subClass.asOWLClass().getIRI());
		}
	    }
	}
	classChildrenCount = subClasses;
	return subClasses;

    }

    public double classInheritenceRichnessMetric(OWLOntology ontology, IRI iri) {

	int countClasses = baseMetric.getTotalClassesCount();
	int countSubClasses = (int) countClassChildrenMetric(ontology, iri);

	// avoid a division by zero
	if (countSubClasses == 0) {
	    classInheritanceRichness = 0.0;
	} else {
	    classInheritanceRichness = OntologyUtility.roundByGlobNK((double) countClasses / (double) countSubClasses);
	}
	return classInheritanceRichness;
    }

    public int classReadabilityMetric(OWLOntology ontology, IRI iri) {
	OWLClass cls = OntologyUtility.getClass(ontology, iri);
	int countAnnotations = 0;

	if (cls != null) {
	    // MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    for (OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ontology)) {
		if (annotation.getProperty().isLabel() || annotation.getProperty().isComment()) {
		    countAnnotations++;
		}
	    }
	}
	classReadability = countAnnotations;
	return countAnnotations;
    }

    public int classRelationshipRichnessMetric(OWLOntology ontology, IRI iri) {
	int noRelations = 0;
	OWLClass next = OntologyUtility.getClass(ontology, iri);
	for (OWLObjectProperty objProperty : next.getObjectPropertiesInSignature()) {
	    if (EntitySearcher.isFunctional(objProperty, ontology/* .getImports() */)) {
		noRelations += next.getObjectPropertiesInSignature().size();
	    }
	}
	return noRelations;
    }

    public int countClassInstancesMetric(OWLOntology ontology, IRI iri) {
	int instances = 0;

	OWLClass cls = OntologyUtility.getClass(ontology, iri);

	if (cls != null) {
	    // MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    instances = EntitySearcher.getIndividuals(cls, ontology).size();

	    // MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    if (!EntitySearcher.getSubClasses(cls, ontology).isEmpty()) {

		// MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
		for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls, ontology)) {
		    instances = instances + countClassInstancesMetric(ontology, subClass.asOWLClass().getIRI());
		}
	    }
	}
	classInstancesCount = instances;
	return instances;
    }

    public int countClassPropertiesMetric(OWLOntology ontology, IRI iri) {
	int properties = 0;

	OWLClass cls = OntologyUtility.getClass(ontology, iri);

	if (cls != null) {
	    // MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    properties = cls.getDatatypesInSignature().size();

	    // MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    if (!EntitySearcher.getSubClasses(cls, ontology).isEmpty()) {

		// MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
		for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls, ontology)) {
		    properties = properties + countClassPropertiesMetric(ontology, subClass.asOWLClass().getIRI());
		}
	    }
	}
	classPropertiesCount = properties;
	return properties;
    }
}