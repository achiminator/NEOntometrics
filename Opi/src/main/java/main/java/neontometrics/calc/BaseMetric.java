package main.java.neontometrics.calc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.semanticweb.owlapi.metrics.AxiomCount;
import org.semanticweb.owlapi.metrics.DLExpressivity;
import org.semanticweb.owlapi.metrics.LogicalAxiomCount;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OWLClassExpressionCollector;

import main.java.neontometrics.calc.handler.OntologyUtility;

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
	calculateImports();
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
	this.returnObject.put("dlExpressivity", expr.getValue());
    }
    public void calculateImports() {
	Set<OWLImportsDeclaration> declaredImports = ontology.getImportsDeclarations();
	Set<String> declaredImportStrings = new HashSet<>();
	for (OWLImportsDeclaration owlImportsDeclaration : declaredImports) {
	    declaredImportStrings.add(owlImportsDeclaration.getIRI().toString());
	}
	this.returnObject.put("declaredImports", declaredImportStrings);
	
	Set<String> directImports = new HashSet<>();
	Set<org.semanticweb.owlapi.model.IRI> directImportIris = ontology.getDirectImportsDocuments();
	for (org.semanticweb.owlapi.model.IRI iri : directImportIris) {
	    directImports.add(iri.toString());
	}
	this.returnObject.put("directImports", directImports);
	
	Set<String> allActualImports = new HashSet<>();
	Set<OWLOntology> allActualImportOntologies = ontology.getImports();
	for (OWLOntology importOntology : allActualImportOntologies) {
	    allActualImports.add(importOntology.getOntologyID().getOntologyIRI().get().toString());
	}
	this.returnObject.put("allActualImports", allActualImports);

    }


}
