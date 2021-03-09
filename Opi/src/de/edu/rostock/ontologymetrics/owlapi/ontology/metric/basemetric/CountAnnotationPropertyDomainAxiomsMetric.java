package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountAnnotationPropertyDomainAxiomsMetric extends AnnotationAxiomsMetric {

	public CountAnnotationPropertyDomainAxiomsMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		//MLIC: changed getAxiomCount with boolean is deprecated
		//return ontology.getAxiomCount(AxiomType.ANNOTATION_PROPERTY_DOMAIN, true);
		return ontology.getAxiomCount(AxiomType.ANNOTATION_PROPERTY_DOMAIN, OntologyUtility.ImportClosures(true));
	}

	@Override
	public String toString() {
		return "Annotation property domain axioms count";
	}

	@Override
	public String getLabel() {
		return "Annotation axioms";
	}

}
