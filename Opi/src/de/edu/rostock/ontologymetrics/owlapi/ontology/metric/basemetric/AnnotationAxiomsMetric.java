package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.OntologyMetric;

public abstract class AnnotationAxiomsMetric extends OntologyMetric {

    public AnnotationAxiomsMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public String getLabel() {
	return "Annotation axioms";
    }
}
