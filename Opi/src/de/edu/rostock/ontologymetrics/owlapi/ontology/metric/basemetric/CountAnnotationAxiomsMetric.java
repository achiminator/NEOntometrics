package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;

public class CountAnnotationAxiomsMetric extends AnnotationAxiomsMetric {

    public CountAnnotationAxiomsMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
	return ontology.getAnnotations().size();
    }

    @Override
    public String toString() {
	return "Annotation axioms count";
    }

}
