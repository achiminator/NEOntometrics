package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class DensityMetric extends GraphMetric {

    public DensityMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
	return 0;
    }

    @Override
    public String toString() {
	return "Density";
    }

}
