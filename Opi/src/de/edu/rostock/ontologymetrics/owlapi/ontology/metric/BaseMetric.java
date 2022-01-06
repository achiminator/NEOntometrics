package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.metrics.AxiomCount;
import org.semanticweb.owlapi.metrics.DLExpressivity;
import org.semanticweb.owlapi.metrics.LogicalAxiomCount;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLClassExpressionCollector;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class BaseMetric extends MetricCalculations implements Callable<BaseMetric> {
    public BaseMetric(OWLOntology ontology, boolean imports) {
	super(ontology, imports);
    }

    public BaseMetric call() {
	calculateCountAxiomsMetric();
	calculateCountLogicalAxiomsMetric();
	calculateCountClasses();
	calculateCountObjectProperties();
	calculateCountDataProperties();
	calculateCountIndividuals();
	calculateDLExpressivity();
	calculateClassesWith();
	calculateAnonymousClasses();
	return (this);
    }

    /**
     * calculates the Number of Axioms
     * 
     */
    public void calculateCountAxiomsMetric() {

	if (imports) {
	    AxiomCount ax = new AxiomCount(ontology);
	    ax.setImportsClosureUsed(true);
	    this.returnObject.put("axioms", ax.getValue());

	} else
	    this.returnObject.put("axioms", new AxiomCount(ontology).getValue());
    }

    /**
     * Calculates the number of Classes
     * 
     * 
     */
    public void calculateCountClasses() {
	this.returnObject.put("classes",
		ontology.getClassesInSignature(OntologyUtility.ImportClosures(imports)).size());
    }

    /**
     * Calculates the number of Anonymous classes out of Class Expressions in an
     * Ontology. *
     */
    public void calculateAnonymousClasses() {
	int anonClassCounter = 0;
	OWLClassExpressionCollector collector = new OWLClassExpressionCollector();
	Set<OWLClassExpression> expressions = collector.visit(ontology);
	for (OWLClassExpression expression : expressions) {
	    if (expression.isAnonymous())
		anonClassCounter++;
	}
	this.returnObject.put("anonymousClasses", anonClassCounter);
    }

    public void calculateClassesWith() {
	int classesWithIndividuals = 0;
	int classesWithSubClasses = 0;
	int classesWithMultipleInheritance = 0;
	int superClassesOfCLassesWithMultipleInheritance = 0;
	int maxSubClasses = 0;
	int superClassCount = 0;
	Map<IRI, List<OWLClass>> objectPropertyToClassMapping = new LinkedHashMap<IRI, List<OWLClass>>();
	Set<OWLClass> classes = ontology.getClassesInSignature(OntologyUtility.ImportClosures(imports));
	for (OWLClass owlClass : classes) {
	    if (owlClass.getIndividualsInSignature().size() > 0)
		classesWithIndividuals++;

	    // This part is responsible for finding how much classes share relations with
	    // each other. Relations are mostly modeled using anonymous superclasses. An
	    // example is the modeling of relations in Protege. There, you mostly create
	    // relations with the "SubClassOf" (thus superclasses) field. The relations in
	    // these anonomous superclasses are crawled by the next entry
	    Collection<OWLClassExpression> superClassExpr = EntitySearcher.getSuperClasses(owlClass, ontology);
	    Collection<OWLObjectProperty> oProperties = OntologyUtility.classExpr2ObjectProperties(superClassExpr);
	    // At first we inverse the direction "classes HAVE relations" to the opposite
	    // "relations HAVE classes"
	    for (OWLObjectProperty oProperty : oProperties) {
		if (!objectPropertyToClassMapping.containsKey(oProperty.getIRI()))
		    objectPropertyToClassMapping.put(oProperty.getIRI(), new ArrayList<OWLClass>());
		objectPropertyToClassMapping.get(oProperty.getIRI()).add(owlClass);
	    }
	    Collection<OWLClassExpression> subClassExpr = EntitySearcher.getSubClasses(owlClass, ontology);
	    Collection<OWLClass> subClasses = OntologyUtility.classExpr2classes(subClassExpr);
	    if (subClasses.size() > 0) {
		classesWithSubClasses++;
		if (subClasses.size() > maxSubClasses)
		    maxSubClasses = subClasses.size();
	    }
	    Collection<OWLClass> superClasses = OntologyUtility
		    .classExpr2classes(EntitySearcher.getSuperClasses(owlClass, ontology));
	    superClassCount += superClasses.size();
	    if (superClasses.size() > 1) {
		classesWithMultipleInheritance++;
		superClassesOfCLassesWithMultipleInheritance += superClasses.size();
	    }

	}
	// ** First finish the "classes with shared Relation calculation**
	// This "Set" element will later on contain all classes that share a relation
	// with other elements.
	Set<OWLClass> classesWithSharedRelations = new HashSet<OWLClass>();
	for (List<OWLClass> classesWithGivenRelation : objectPropertyToClassMapping.values()) {
	    // If a relation is used in more than two classes, these classes share this
	    // relation
	    if (classesWithGivenRelation.size() >= 2)
		classesWithSharedRelations.addAll(classesWithGivenRelation);
	}
	this.returnObject.put("classesThatShareARelation", classesWithSharedRelations.size());

	this.returnObject.put("superClasses", superClassCount);
	this.returnObject.put("classesWithIndividuals", classesWithIndividuals);
	this.returnObject.put("classesWithSubClassess", classesWithSubClasses);
	this.returnObject.put("classesWithMultipleInheritance", classesWithMultipleInheritance);
	this.returnObject.put("superClassesOfClassesWithMultipleInheritance",
		superClassesOfCLassesWithMultipleInheritance);
	this.returnObject.put("maxSubclassesOfAClass", maxSubClasses);
    }

    /**
     * Calculates the number of Object Properties
     * 
     */
    public void calculateCountObjectProperties() {
	this.returnObject.put("objectProperties",
		ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(imports)).size());
    }

    /***
     * Counts the number of Data Properties
     * 
     */
    public void calculateCountDataProperties() {
	this.returnObject.put("dataProperties",
		ontology.getDataPropertiesInSignature(OntologyUtility.ImportClosures(imports)).size());

    }

    /***
     * Counts the number of logical Axioms (Without annoations) *
     */
    public void calculateCountLogicalAxiomsMetric() {

	LogicalAxiomCount lg = new LogicalAxiomCount(ontology);
	lg.setImportsClosureUsed(imports);
	this.returnObject.put("logicalAxioms", lg.getValue());

    }

    /**
     * Count the number of individuals (Instances)
     * 
     */
    public void calculateCountIndividuals() {
	this.returnObject.put("individuals",
		ontology.getIndividualsInSignature(OntologyUtility.ImportClosures(imports)).size());
    }

    /**
     * Calculates the Description Logic Expressivity of the Ontology (e.g., SHOIN,
     * ALC)
     * 
     * @param ontology
     */
    public void calculateDLExpressivity() {
	DLExpressivity expr = new DLExpressivity(ontology);
	System.out.println(expr.recomputeMetric());
	this.returnObject.put("DLExpressivity", expr.getValue());
    }

}
