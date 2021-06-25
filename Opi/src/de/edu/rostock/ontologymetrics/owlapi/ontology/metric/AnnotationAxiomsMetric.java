package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class AnnotationAxiomsMetric {
    private int annotationAxiomsCount;
    private int annotationAssertionAxiomsCount;
    private int annotationPropertyDomainAxiomsCount;
    private int annotationPropertyRangeAxiomsCount;

    public Map<String, Object> getAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("Annotationaxiomscount", annotationAxiomsCount);
	returnObject.put("Annotationassertionaxiomscount", annotationAssertionAxiomsCount);
	returnObject.put("Annotationpropertydomainaxiomscount", annotationPropertyDomainAxiomsCount);
	returnObject.put("Annotationpropertyrangeaxiomscount", annotationPropertyRangeAxiomsCount);
	return returnObject;
    }

    public Map<String, Object> calculateAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("Annotationaxiomscount", countAnnotationAxiomsMetric(ontology));
	returnObject.put("Annotationassertionaxiomscount", countAnnotationAssertionAxiomsMetric(ontology));
	returnObject.put("Annotationpropertydomainaxiomscount", countAnnotationPropertyDomainAxiomsMetric(ontology));
	returnObject.put("Annotationpropertyrangeaxiomscount", countAnnotationPropertyRangeAxiomsMetric(ontology));
	return returnObject;
    }

    public int countAnnotationAxiomsMetric(OWLOntology ontology) {
	annotationAxiomsCount = ontology.getAnnotations().size();
	return annotationAxiomsCount;
    }

    public int countAnnotationAssertionAxiomsMetric(OWLOntology ontology) {
	annotationAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.ANNOTATION_ASSERTION);
	return annotationAssertionAxiomsCount;
    }

    public int countAnnotationPropertyDomainAxiomsMetric(OWLOntology ontology) {
	annotationPropertyDomainAxiomsCount = ontology.getAxiomCount(AxiomType.ANNOTATION_PROPERTY_DOMAIN,
		OntologyUtility.ImportClosures(true));
	return annotationPropertyDomainAxiomsCount;
    }

    public int countAnnotationPropertyRangeAxiomsMetric(OWLOntology ontology) {
	annotationPropertyRangeAxiomsCount = ontology.getAxiomCount(AxiomType.ANNOTATION_PROPERTY_RANGE,
		OntologyUtility.ImportClosures(true));
	return annotationPropertyRangeAxiomsCount;
    }
}
