package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class ModularityMetric extends GraphMetric {

    public ModularityMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Float getValue() {
	return 0.0F;
    }

    @Override
    public String toString() {
	return "Modularity";
    }

}
