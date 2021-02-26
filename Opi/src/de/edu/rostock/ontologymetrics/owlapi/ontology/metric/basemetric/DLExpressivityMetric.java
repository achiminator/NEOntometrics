package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.metrics.DLExpressivity;
import org.semanticweb.owlapi.model.OWLOntology;

public class DLExpressivityMetric extends BaseMetric {

	Logger myLogger = Logger.getLogger(this.getClass());
	
	public DLExpressivityMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public String getValue() {
		// MLIC new implemented
	    DLExpressivity expr = new DLExpressivity(ontology);
	    System.out.println(expr.recomputeMetric());
		return (expr.getValue());
	}

	@Override
	public String toString() {
		return "DL expressivity";
	}
}
