package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class TanglednessMetric extends GraphMetric {

	Logger myLogger = Logger.getLogger(this.getClass());

	public TanglednessMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Double getValue() {
		return OntologyUtility
				.roundByGlobNK((double) parserI.getTangledClasses().size() / (double) parserI.getNoClasses()); //with imports
	}

	@Override
	public String toString() {
		return "Tangledness";
	}

}
