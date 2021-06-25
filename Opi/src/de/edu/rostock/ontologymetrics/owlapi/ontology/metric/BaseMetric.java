package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.metrics.AxiomCount;
import org.semanticweb.owlapi.metrics.DLExpressivity;
import org.semanticweb.owlapi.metrics.LogicalAxiomCount;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class BaseMetric implements Callable<BaseMetric>{

    public BaseMetric(OWLOntology ontology) {
	this.ontology = ontology;
    }

    public int getAxioms() {
	return axioms;
    }

    public int getClassCount() {
	return classCount;
    }

    public int getTotalClassesCount() {
	return totalClassesCount;
    }

    public int getObjectPropertyCount() {
	return objectPropertyCount;
    }

    public int getTotalObjectPropertyCount() {
	return totalObjectPropertyCount;
    }

    public int getDataPropertyCount() {
	return dataPropertyCount;
    }

    public int getTotalDataPropertyCount() {
	return totalDataPropertyCount;
    }

    public int getLogicalAxiomsCount() {
	return logicalAxiomsCount;
    }

    public int getIndividualCount() {
	return individualCount;
    }

    public int getTotalIndividualCount() {
	return totalIndividualCount;
    }

    public String getDlExpressivity() {
	return dlExpressivity;
    }

    private int axioms;
    private int classCount;
    private int totalClassesCount;
    private int objectPropertyCount;
    private int totalObjectPropertyCount;
    private int dataPropertyCount;
    private int totalDataPropertyCount;
    private int logicalAxiomsCount;
    private int individualCount;
    private int totalIndividualCount;
    private OWLOntology ontology;
    private String dlExpressivity;

    public Map<String, Object> getAllMetrics() {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("Axioms", axioms);
	returnObject.put("Logicalaxiomscount", logicalAxiomsCount);
	returnObject.put("Classcount", classCount);
	returnObject.put("Totalclassescount", totalClassesCount);
	returnObject.put("Objectpropertycount", objectPropertyCount);
	returnObject.put("Totalobjectpropertiescount", totalObjectPropertyCount);
	returnObject.put("Datapropertycount", dataPropertyCount);
	returnObject.put("Totaldatapropertiescount", totalDataPropertyCount);
	returnObject.put("Propertiescount",(int) returnObject.get("Totaldatapropertiescount") +  (int) returnObject.get("Totalobjectpropertiescount"));
	returnObject.put("Individualcount", individualCount);
	returnObject.put("Totalindividualscount", totalIndividualCount);
	returnObject.put("DLexpressivity", dlExpressivity);
	return (returnObject);
    }

    public BaseMetric call() {
	calculateCountAxiomsMetric(ontology);
	calculateCountLogicalAxiomsMetric(ontology);
	calculateCountClasses(ontology);
	calculateCountTotalClasses(ontology);
	calculateCountObjectProperties(ontology);
	calculateCountTotalObjectProperties(ontology);
	calculateCountDataProperties(ontology);
	calculateCountTotalDataProperties(ontology);
	calculateCountIndividuals(ontology);
	calculateCountTotalIndividuals(ontology);
	calculateDLExpressivity(ontology);
	return (this);
    }

    public int calculateCountAxiomsMetric(OWLOntology ontology) {
	this.axioms = new AxiomCount(ontology).getValue();
	return (axioms);
    }

    public int calculateCountClasses(OWLOntology ontology) {
	this.classCount = ontology.getClassesInSignature().size();
	return (this.classCount);
    }

    public int calculateCountTotalClasses(OWLOntology ontology) {
	this.totalClassesCount = ontology.getClassesInSignature(OntologyUtility.ImportClosures(true)).size();
	return (this.totalClassesCount);
    }

    public int calculateCountObjectProperties(OWLOntology ontology) {
	this.objectPropertyCount = ontology.getObjectPropertiesInSignature().size();
	return this.objectPropertyCount;
    }

    public int calculateCountTotalObjectProperties(OWLOntology ontology) {
	this.totalObjectPropertyCount = ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true))
		.size();
	return this.totalObjectPropertyCount;
    }

    public int calculateCountDataProperties(OWLOntology ontology) {
	this.dataPropertyCount = ontology.getDataPropertiesInSignature().size();
	return (this.dataPropertyCount);
    }

    public int calculateCountTotalDataProperties(OWLOntology ontology) {
	this.totalDataPropertyCount = ontology.getDataPropertiesInSignature(OntologyUtility.ImportClosures(true))
		.size();
	return this.totalDataPropertyCount;
    }

    public int calculateCountLogicalAxiomsMetric(OWLOntology ontology) {
	this.logicalAxiomsCount = new LogicalAxiomCount(ontology).getValue();
	return this.logicalAxiomsCount;
    }

    public int calculateCountIndividuals(OWLOntology ontology) {
	ontology.getIndividualsInSignature().size();
	return this.individualCount;
    }

    public int calculateCountTotalIndividuals(OWLOntology ontology) {
	totalIndividualCount = ontology.getIndividualsInSignature(OntologyUtility.ImportClosures(true)).size();
	return this.totalIndividualCount;
    }

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
