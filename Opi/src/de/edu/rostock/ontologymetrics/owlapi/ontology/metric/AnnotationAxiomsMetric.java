package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class AnnotationAxiomsMetric implements Callable<AnnotationAxiomsMetric> {
    private int annotationAxiomsCount;
    private int annotationAssertionAxiomsCount;
    private int annotationPropertyDomainAxiomsCount;
    private int annotationPropertyRangeAxiomsCount;
    private OWLOntology ontology;
    public AnnotationAxiomsMetric(OWLOntology ontology) {
	this.ontology = ontology;
    }
    public Map<String, Object> getAllMetrics() {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("Annotationaxiomscount", annotationAxiomsCount);
	returnObject.put("Annotationassertionaxiomscount", annotationAssertionAxiomsCount);
	returnObject.put("Annotationpropertydomainaxiomscount", annotationPropertyDomainAxiomsCount);
	returnObject.put("Annotationpropertyrangeaxiomscount", annotationPropertyRangeAxiomsCount);
	return returnObject;
    }

    public AnnotationAxiomsMetric call() {

	countAnnotationAxiomsMetric(ontology);
	countAnnotationAssertionAxiomsMetric(ontology);
	countAnnotationPropertyDomainAxiomsMetric(ontology);
	countAnnotationPropertyRangeAxiomsMetric(ontology);
	return this;
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
