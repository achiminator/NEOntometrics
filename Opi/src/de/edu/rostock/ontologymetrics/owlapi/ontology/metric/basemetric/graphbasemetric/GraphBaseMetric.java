package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.BaseMetric;

public abstract class GraphBaseMetric extends BaseMetric {

    public GraphBaseMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public String getLabel() {
	return "Graph metrics";
    }

}
