package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class CountPropertiesMetric extends BaseMetric {

    public CountPropertiesMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
	CountObjectPropertiesMetric objectPropertiesMetric = new CountObjectPropertiesMetric(
		ontology);
	CountDataPropertiesMetric dataPropertiesMetric = new CountDataPropertiesMetric(
		ontology);

	return objectPropertiesMetric.getValue()
		+ dataPropertiesMetric.getValue();
    }

    @Override
    public String toString() {
	return "Properties count";
    }

}
