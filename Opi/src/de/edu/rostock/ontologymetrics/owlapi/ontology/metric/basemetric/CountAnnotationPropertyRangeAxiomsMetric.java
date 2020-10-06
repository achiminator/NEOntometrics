package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountAnnotationPropertyRangeAxiomsMetric extends AnnotationAxiomsMetric {

    public CountAnnotationPropertyRangeAxiomsMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
    	//MLIC: changed getAxiomCount with boolean is deprecated
    	//return ontology.getAxiomCount(AxiomType.ANNOTATION_PROPERTY_RANGE, true);
    	return ontology.getAxiomCount(AxiomType.ANNOTATION_PROPERTY_RANGE, OntologyUtility.ImportClosures(true));
    }

    @Override
    public String toString() {
	return "Annotation property range axioms count";
    }

    @Override
    public String getLabel() {
	return "Annotation axioms";
    }

}
