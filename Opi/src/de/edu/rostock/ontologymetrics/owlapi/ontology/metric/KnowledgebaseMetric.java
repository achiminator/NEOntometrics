package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.GraphParser;

public class KnowledgebaseMetric extends OntologyMetric {

    Map<String, Object> metrics;

    public KnowledgebaseMetric(Map<String, Object> metrics) {
	this.metrics = metrics;
    }

    public Map<String, Object> getAllMetrics(OWLOntology ontology, GraphParser parser, GraphParser parserI) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("Average population", metrics);
	returnObject.put("Class richness", classRichnessMetric(ontology, metrics));
	return returnObject;
    }

    public String cohesionMetric(Map<String, Object> metrics) {
	double avg_depth = (double) metrics.get("Average depth");
	int roots = (int) metrics.get("Absolute root cardinality");
	int leaves = (int) metrics.get("Absolute leaf cardinality");

	return "NoR: "
		+ roots
		+ " - NoL: "
		+ leaves
		+ " - ADIT-LN: "
		+ OntologyUtility.roundByGlobNK(avg_depth);
    }

    public double averagePopulationMetric(Map<String, Object> metrics) {
	int countTotalIndividuals = (int) metrics.get("Total individuals count");
	int countTotalClasses = (int) metrics.get("Total classes count");

	myLogger.debug("countTotalIndividuals: "
		+ countTotalIndividuals);
	myLogger.debug("countTotalClasses: "
		+ countTotalClasses);

	// avoid a division by zero
	if (countTotalClasses == 0) {
	    return 0.0;
	} else {
	    return OntologyUtility.roundByGlobNK(countTotalIndividuals / countTotalClasses);
	}
    }
    public double classRichnessMetric(OWLOntology ontology, Map<String, Object> metric) {
	double countClassHasIndividual = 0.0;
	int totalClasses = (int) metrics.get("Total classes count");
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
		return OntologyUtility.roundByGlobNK((double) countClassHasIndividual / (double) countTotalClasses);
	}
    }
}
