package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class KnowledgebaseMetric extends MetricCalculations implements Callable<KnowledgebaseMetric> {



    
    public Map<String, Object> previousMetrics;
    public KnowledgebaseMetric(Map<String, Object> previousCalculations, OWLOntology ontology, boolean withImports) {
	super(ontology, withImports);
	this.previousMetrics = previousCalculations;
	
	
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

    public void averagePopulationMetric() {
	int countTotalIndividuals = (int) previousMetrics.get("individuals");

	int countTotalClasses = (int) previousMetrics.get("classes");
	    double averagePopulationMetric; 
	// avoid a division by zero
	if (countTotalClasses == 0) {
	    averagePopulationMetric = 0.0;
	} else {
	    averagePopulationMetric = OntologyUtility.roundByGlobNK(countTotalIndividuals / countTotalClasses);
	}
	returnObject.put("averagePopulation", averagePopulationMetric);
	

    }

    public void classRichnessMetric(OWLOntology ontology) {
	double countClassHasIndividual = 0.0;
	double classRichnessMetric; 
	int totalClasses = (int) previousMetrics.get("classes");
	double countTotalClasses = totalClasses;

	// avoid a division by zero
	if (countTotalClasses == 0) {
	    classRichnessMetric=0.0; 
	} else {
	    for (OWLClass owlClass : ontology.getClassesInSignature(OntologyUtility.ImportClosures(true))) {
		if (!EntitySearcher.getIndividuals(owlClass, ontology).isEmpty()) {
		    countClassHasIndividual++;
		}
	    }
	    classRichnessMetric = OntologyUtility
		    .roundByGlobNK((double) countClassHasIndividual / (double) countTotalClasses);
	}
	returnObject.put("classRichness", classRichnessMetric);
	
    }
}
