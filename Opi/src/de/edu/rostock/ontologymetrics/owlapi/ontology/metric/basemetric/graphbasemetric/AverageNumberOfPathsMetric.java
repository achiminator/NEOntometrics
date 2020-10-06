package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class AverageNumberOfPathsMetric extends GraphMetric {

	Logger myLogger = Logger.getLogger(this.getClass());
	
	public AverageNumberOfPathsMetric(OWLOntology pOntology) {
		super(pOntology);
	}
	
	 @Override
	    public Double getValue() {
		 	int gen = parserI.getGenerations().size();
		 	int pathsAll = parserI.getPathsAll().size();
		 
			if (gen > 0)
				return OntologyUtility.roundByGlobNK((double)pathsAll / (double)gen);
			else
				return 0.0;
	    }

	    @Override
	    public String toString() {
		return "Average number of paths";
	    }
}
