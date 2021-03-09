package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

public class CountAnnotationAssertionAxiomsMetric extends AnnotationAxiomsMetric {

    public CountAnnotationAssertionAxiomsMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
	return ontology.getAxiomCount(AxiomType.ANNOTATION_ASSERTION);
    }

    @Override
    public String toString() {
	return "Annotation assertion axioms count";
    }

    @Override
    public String getLabel() {
	return "Annotation axioms";
    }

}
