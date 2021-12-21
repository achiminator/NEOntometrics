package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.metrics.AxiomCount;
import org.semanticweb.owlapi.metrics.DLExpressivity;
import org.semanticweb.owlapi.metrics.LogicalAxiomCount;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
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
	    this.returnObject.put("Axioms", ax.getValue());

	} else
	    this.returnObject.put("Axioms", new AxiomCount(ontology).getValue());
    }

    /**
     * Calculates the number of Classes
     * 
     * 
     */
    public void calculateCountClasses() {
	this.returnObject.put("Classcount",
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
	this.returnObject.put("Anonymousclasses", anonClassCounter);
    }

    public void calculateClassesWith() {
	int classesWithIndividuals = 0;
	int classesWithSubClasses = 0;
	int classesWithMultipleInheritance = 0;
	int superClassesOfCLassesWithMultipleInheritance = 0;
	int maxSubClasses = 0;
	int superClassCount = 0;

	Set<OWLClass> classes = ontology.getClassesInSignature(OntologyUtility.ImportClosures(imports));
	for (OWLClass owlClass : classes) {
	    if (owlClass.getIndividualsInSignature().size() > 0)
		classesWithIndividuals++;
	    Collection<OWLClass> subClasses = OntologyUtility
		    .classExpr2classes(EntitySearcher.getSubClasses(owlClass, ontology));
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
	this.returnObject.put("Superclasses", superClassCount);
	this.returnObject.put("Classeswithindividuals", classesWithIndividuals);
	this.returnObject.put("Classeswithsubclassess", classesWithSubClasses);
	this.returnObject.put("Classeswithmultipleinheritance", classesWithMultipleInheritance);
	this.returnObject.put("Superclassesofclasseswithmultipleinheritance",
		superClassesOfCLassesWithMultipleInheritance);
	this.returnObject.put("Maxsubclassesofaclass", maxSubClasses);
    }

    /**
     * Calculates the number of Object Properties
     * 
     */
    public void calculateCountObjectProperties() {
	this.returnObject.put("Objectpropertycount",
		ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(imports)).size());
    }

    /***
     * Counts the number of Data Properties
     * 
     */
    public void calculateCountDataProperties() {
	this.returnObject.put("Datapropertycount",
		ontology.getDataPropertiesInSignature(OntologyUtility.ImportClosures(imports)).size());

    }

    /***
     * Counts the number of logical Axioms (Without annoations) *
     */
    public void calculateCountLogicalAxiomsMetric() {

	LogicalAxiomCount lg = new LogicalAxiomCount(ontology);
	lg.setImportsClosureUsed(imports);
	this.returnObject.put("Logicalaxiomscount", lg.getValue());

    }

    /**
     * Count the number of individuals (Instances)
     * 
     */
    public void calculateCountIndividuals() {
	this.returnObject.put("Individualcount",
		ontology.getIndividualsInSignature(OntologyUtility.ImportClosures(imports)).size());
    }

    /**
     * Counts the overall number of properties
     * 
     * adds dataproperties + objectproperties
     */
    public void countProperties() {
	returnObject.put("Propertiescount",
		(int) returnObject.get("Datapropertycount") + (int) returnObject.get("Objectpropertycount"));
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
