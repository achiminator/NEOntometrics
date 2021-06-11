package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;


public abstract class AnnotationAxiomsMetric {
    public Map<String, Object> getAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("Annotation axioms count", countAnnotationAxiomsMetric(ontology));
	returnObject.put("Annotation assertion axioms count", countAnnotationAssertionAxiomsMetric(ontology));
	returnObject.put("Annotation property domain axioms count", countAnnotationPropertyDomainAxiomsMetric(ontology));
	returnObject.put("Annotation property range axioms count", countAnnotationPropertyRangeAxiomsMetric(ontology));
	return returnObject;
    }
    
    public int countAnnotationAxiomsMetric(OWLOntology ontology) {
	return ontology.getAnnotations().size();
    }

    public int countAnnotationAssertionAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.ANNOTATION_ASSERTION);
    }
    public int countAnnotationPropertyDomainAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.ANNOTATION_PROPERTY_DOMAIN, OntologyUtility.ImportClosures(true));
    }
    public int countAnnotationPropertyRangeAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.ANNOTATION_PROPERTY_RANGE, OntologyUtility.ImportClosures(true));
    }
}
