package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.metrics.AxiomCount;
import org.semanticweb.owlapi.metrics.DLExpressivity;
import org.semanticweb.owlapi.metrics.LogicalAxiomCount;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class BaseMetric implements Callable<BaseMetric> {
    private boolean imports;

    /**
     * Constructor of BaseMetrics Calculation
     * 
     * @param ontology OWLOntology object
     * @param imports  decide whether imports are considered or not
     */
    public BaseMetric(OWLOntology ontology, boolean imports) {
	this.ontology = ontology;
	this.imports = imports;
    }

    public int getAxioms() {
	return axioms;
    }

    public int getClassCount() {
	return classCount;
    }

    public int getObjectPropertyCount() {
	return objectPropertyCount;
    }

    public int getDataPropertyCount() {
	return dataPropertyCount;
    }

    public int getLogicalAxiomsCount() {
	return logicalAxiomsCount;
    }

    public int getIndividualCount() {
	return individualCount;
    }

    public String getDlExpressivity() {
	return dlExpressivity;
    }

    private int axioms;
    private int classCount;
    private int objectPropertyCount;
    private int dataPropertyCount;
    private int logicalAxiomsCount;
    private int individualCount;
    private OWLOntology ontology;
    private String dlExpressivity;

    public Map<String, Object> getAllMetrics() {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("Axioms", axioms);
	returnObject.put("Logicalaxiomscount", logicalAxiomsCount);
	returnObject.put("Classcount", classCount);
	returnObject.put("Objectpropertycount", objectPropertyCount);
	returnObject.put("Datapropertycount", dataPropertyCount);
	returnObject.put("Propertiescount", countProperties());
	returnObject.put("Individualcount", individualCount);
	returnObject.put("DLexpressivity", dlExpressivity);
	return (returnObject);
    }

    public BaseMetric call() {
	calculateCountAxiomsMetric(ontology, imports);
	calculateCountLogicalAxiomsMetric(ontology, imports);
	calculateCountClasses(ontology, imports);
	calculateCountObjectProperties(ontology, imports);
	calculateCountDataProperties(ontology, imports);
	calculateCountIndividuals(ontology, imports);
	calculateDLExpressivity(ontology);
	return (this);
    }

    /**
     * calculates the Number of Axioms
     * 
     * @param ontology
     * @param withImports States if with Imports or without
     * @return Number of Axioms
     */
    public int calculateCountAxiomsMetric(OWLOntology ontology, boolean withImports) {

	if (withImports) {
	    AxiomCount ax = new AxiomCount(ontology);
	    ax.setImportsClosureUsed(true);
	    this.axioms = ax.getValue();
	} else
	    this.axioms = new AxiomCount(ontology).getValue();
	return (axioms);
    }

    /**
     * Calculates the number of Classes
     * 
     * @param ontology
     * @param withImports Either with Imports or without
     * @return Number of Classes
     */
    public int calculateCountClasses(OWLOntology ontology, boolean withImports) {
	if (withImports)
	    this.classCount = ontology.getClassesInSignature(OntologyUtility.ImportClosures(true)).size();
	else
	    this.classCount = ontology.getClassesInSignature().size();
	return (this.classCount);
    }

    /**
     * Calculates the number of Object Properties
     * 
     * @param ontology
     * @param withImports Either with imports or without
     * @return Object Property Axioms
     */
    public int calculateCountObjectProperties(OWLOntology ontology, boolean withImports) {
	if (withImports)
	    this.objectPropertyCount = ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true))
		    .size();
	else
	    this.objectPropertyCount = ontology.getObjectPropertiesInSignature().size();
	return this.objectPropertyCount;
    }

    /***
     * Counts the number of Data Properties
     * 
     * @param ontology
     * @param withImports
     * @return
     */
    public int calculateCountDataProperties(OWLOntology ontology, boolean withImports) {
	if (withImports)
	    this.dataPropertyCount = ontology.getDataPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();
	else
	    this.dataPropertyCount = ontology.getDataPropertiesInSignature().size();
	return (this.dataPropertyCount);
    }

    /***
     * Counts the number of logical Axioms (Without annoations)
     * 
     * @param ontology
     * @param withImports
     * @return
     */
    public int calculateCountLogicalAxiomsMetric(OWLOntology ontology, boolean withImports) {
	if (withImports) {
	    LogicalAxiomCount lg = new LogicalAxiomCount(ontology);
	    lg.setImportsClosureUsed(true);
	    this.logicalAxiomsCount = lg.getValue();
	} else
	    this.logicalAxiomsCount = new LogicalAxiomCount(ontology).getValue();
	return this.logicalAxiomsCount;
    }

    /**
     * Count the number of individuals (Instances)
     * 
     * @param ontology
     * @param withImports
     * @return
     */
    public int calculateCountIndividuals(OWLOntology ontology, boolean withImports) {
	if (withImports)
	    this.individualCount = ontology.getIndividualsInSignature(OntologyUtility.ImportClosures(true)).size();
	else
	    this.individualCount = ontology.getIndividualsInSignature().size();

	return this.individualCount;
    }

    /**
     * Counts the overall number of properties
     * 
     * @return dataproperties + objectproperties
     */
    public int countProperties() {
	return (dataPropertyCount + objectPropertyCount);
    }

    public String calculateDLExpressivity(OWLOntology ontology) {
	DLExpressivity expr = new DLExpressivity(ontology);
	System.out.println(expr.recomputeMetric());
	dlExpressivity = expr.getValue();
	return (dlExpressivity);
    }

}
