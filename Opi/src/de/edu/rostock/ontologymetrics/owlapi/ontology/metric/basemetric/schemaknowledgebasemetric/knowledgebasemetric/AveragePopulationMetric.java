package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.knowledgebasemetric;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountTotalClassesMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountTotalIndividualsMetric;

public class AveragePopulationMetric extends KnowledgebaseMetric {

	Logger myLogger = Logger.getLogger(this.getClass()); 
	
	public AveragePopulationMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Double getValue() {
		double countTotalIndividuals = new CountTotalIndividualsMetric(ontology).getValue();
		double countTotalClasses = new CountTotalClassesMetric(ontology).getValue();

		myLogger.debug("countTotalIndividuals: "+countTotalIndividuals);
		myLogger.debug("countTotalClasses: "+countTotalClasses);
		
		// avoid a division by zero
		if (countTotalClasses == 0) {
			return 0.0;
		} else {
			return OntologyUtility.roundByGlobNK(countTotalIndividuals / countTotalClasses);
		}
	}

	@Override
	public String toString() {
		return "Average population";
	}

}
