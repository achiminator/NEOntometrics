package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.HashMap;
import java.util.Map;
import org.semanticweb.owlapi.metrics.AxiomCount;
import org.semanticweb.owlapi.metrics.DLExpressivity;
import org.semanticweb.owlapi.metrics.LogicalAxiomCount;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;


public abstract class BaseMetric {

    public BaseMetric() {
    }

    public Map<String, Object> getAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("Axioms", countAxiomsMetric(ontology));
	returnObject.put("Class count", countClasses(ontology));
	returnObject.put("Data property count", countDataProperties(ontology));
	returnObject.put("Total classes count", countTotalClasses(ontology));
	returnObject.put("Object property count", countObjectProperties(ontology));
	returnObject.put("Total object properties count", countTotalObjectProperties(ontology));
	returnObject.put("Total data properties count", countTotalDataProperties(ontology));
	returnObject.put("Object property count", countProperties(returnObject));
	returnObject.put("Logical axioms count", CountLogicalAxiomsMetric(ontology));
	returnObject.put("Individual count", countIndividuals(ontology));
	returnObject.put("Total individuals count", countTotalIndividuals(ontology));
	returnObject.put("DL expressivity", DLExpressivity(ontology));
	return(returnObject);
    }

    public int countAxiomsMetric(OWLOntology ontology) {
	return (new AxiomCount(ontology).getValue());
    }

    public int countClasses(OWLOntology ontology) {
	return (ontology.getClassesInSignature().size());
    }

    public int countTotalClasses(OWLOntology ontology) {
	return (ontology.getClassesInSignature(OntologyUtility.ImportClosures(true)).size());
    }

    public int countObjectProperties(OWLOntology ontology) {
	return ontology.getObjectPropertiesInSignature().size();
    }

    public int countTotalObjectProperties(OWLOntology ontology) {
	return ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();
    }

    public int countDataProperties(OWLOntology ontology) {
	return (ontology.getDataPropertiesInSignature().size());
    }

    public int countTotalDataProperties(OWLOntology ontology) {
	return ontology.getDataPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();
    }

    public int CountLogicalAxiomsMetric(OWLOntology ontology) {
	return (new LogicalAxiomCount(ontology).getValue());
    }

    public int countIndividuals(OWLOntology ontology) {
	return ontology.getIndividualsInSignature().size();
    }

    public int countTotalIndividuals(OWLOntology ontology) {
	return ontology.getIndividualsInSignature(OntologyUtility.ImportClosures(true)).size();
    }

    public int countProperties(Map<String, Object> returnObject) {
	return ((int)returnObject.get("Data property count") + (int)returnObject.get("Object property count"));
    }

    public String DLExpressivity(OWLOntology ontology) {
	DLExpressivity expr = new DLExpressivity(ontology);
	System.out.println(expr.recomputeMetric());
	return (expr.getValue());
    }

}
