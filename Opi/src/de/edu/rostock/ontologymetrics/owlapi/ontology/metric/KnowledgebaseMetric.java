package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class KnowledgebaseMetric implements Callable<KnowledgebaseMetric> {

    public double getAveragePopulationMetric() {
	return averagePopulationMetric;
    }

    public double getClassRichnessMetric() {
	return classRichnessMetric;
    }

    private BaseMetric baseMetrics;

    private double averagePopulationMetric;
    private double classRichnessMetric;
    OWLOntology ontology;

    public KnowledgebaseMetric(BaseMetric baseMetrics, OWLOntology ontology) {
	super();
	this.baseMetrics = baseMetrics;
	this.ontology = ontology;
    }

    public Map<String, Object> getAllMetrics() {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("Averagepopulation", averagePopulationMetric);
	returnObject.put("Classrichness", classRichnessMetric);
	return returnObject;
    }

    public KnowledgebaseMetric call() {
	averagePopulationMetric();
	classRichnessMetric(ontology);
	return this;
    }

//    public String cohesionMetric(Map<String, Object> metrics) {
//	double avg_depth = (double) metrics.get("Average depth");
//	int roots = (int) metrics.get("Absolute root cardinality");
//	int leaves = (int) metrics.get("Absolute leaf cardinality");
//
//	return "NoR: "
//		+ roots
//		+ " - NoL: "
//		+ leaves
//		+ " - ADIT-LN: "
//		+ OntologyUtility.roundByGlobNK(avg_depth);
//    }

    public double averagePopulationMetric() {
	int countTotalIndividuals = baseMetrics.getClassCount();

	int countTotalClasses = baseMetrics.getClassCount();


	// avoid a division by zero
	if (countTotalClasses == 0) {
	    averagePopulationMetric = 0.0;
	} else {
	    averagePopulationMetric = OntologyUtility.roundByGlobNK(countTotalIndividuals / countTotalClasses);
	}
	return averagePopulationMetric;
    }

    public double classRichnessMetric(OWLOntology ontology) {
	double countClassHasIndividual = 0.0;
	int totalClasses = baseMetrics.getClassCount();
	double countTotalClasses = totalClasses;

	// avoid a division by zero
	if (countTotalClasses == 0) {
	    return 0.0;
	} else {
	    for (OWLClass owlClass : ontology.getClassesInSignature(OntologyUtility.ImportClosures(true))) {
		if (!EntitySearcher.getIndividuals(owlClass, ontology).isEmpty()) {
		    countClassHasIndividual++;
		}
	    }
	    classRichnessMetric = OntologyUtility
		    .roundByGlobNK((double) countClassHasIndividual / (double) countTotalClasses);
	}
	return classRichnessMetric;
    }
}
