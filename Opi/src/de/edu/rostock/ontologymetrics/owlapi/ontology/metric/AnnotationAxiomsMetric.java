package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class AnnotationAxiomsMetric implements Callable<AnnotationAxiomsMetric> {
    private int annotationAxiomsCount;
    private int annotationAssertionAxiomsCount;
    private int annotationPropertyDomainAxiomsCount;
    private int annotationPropertyRangeAxiomsCount;

    private OWLOntology ontology;
    private boolean withImports;
    private int classAnnoation;
    private int datatypeAnnotation;
    private int dataPropertyAnnotation;
    private int objectPropertyAnnotation;

    /**
     * Counts the number of Annotations
     * 
     * @param ontology
     * @param withImports States whether you want to consider Imports as well or not
     */
    public AnnotationAxiomsMetric(OWLOntology ontology, boolean withImports) {
	this.ontology = ontology;
	this.withImports = withImports;
    }

    public Map<String, Object> getAllMetrics() {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("Annotationaxiomscount", annotationAxiomsCount);
	returnObject.put("Annotationassertionaxiomscount", annotationAssertionAxiomsCount);
	returnObject.put("Annotationpropertydomainaxiomscount", annotationPropertyDomainAxiomsCount);
	returnObject.put("Annotationpropertyrangeaxiomscount", annotationPropertyRangeAxiomsCount);
	returnObject.put("Datapropertyannotation", dataPropertyAnnotation);
	returnObject.put("Classannotation", classAnnoation);
	returnObject.put("Objectpropertyannotation", objectPropertyAnnotation);
	returnObject.put("Datatypeannotation", datatypeAnnotation);

	return returnObject;
    }

    public AnnotationAxiomsMetric call() {
	countAnnotationAxiomsMetric(ontology);
	countAnnotationAssertionAxiomsMetric(ontology, withImports);
	countAnnotationPropertyDomainAxiomsMetric(ontology, withImports);
	countAnnotationPropertyRangeAxiomsMetric(ontology, withImports);
	iterativeAnnotationMetrics(true);
	return this;
    }

    /**
     * Counts the Annotations that describe the Ontology in general (not Annoations
     * per class)
     * 
     * @param ontology
     * @return
     */
    public int countAnnotationAxiomsMetric(OWLOntology ontology) {
	annotationAxiomsCount = ontology.getAnnotations().size();
	return annotationAxiomsCount;
    }

    /**
     * Some of the Annotationmetrics (the count of the detailed property and class
     * annotations)need to be run by iteration over the elements. These are done
     * then done all at once.
     * 
     * @param withImports
     */
    public void iterativeAnnotationMetrics(boolean withImports) {
	Set<OWLAnnotationAssertionAxiom> annotationAxioms = ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION,
		OntologyUtility.ImportClosures(withImports));
	for (OWLAnnotationAssertionAxiom owlAnnotationAssertionAxiom : annotationAxioms) {
	    if (!(owlAnnotationAssertionAxiom.getSubject() instanceof OWLAnonymousIndividual)) {
		Set<OWLEntity> entities = ontology
			.getEntitiesInSignature((IRI) owlAnnotationAssertionAxiom.getSubject());
		for (OWLEntity entity : entities) {
		    if (entity.isOWLClass())
			classAnnoation++;
		    else if (entity.isOWLDatatype())
			datatypeAnnotation++;
		    else if (entity.isOWLDataProperty())
			dataPropertyAnnotation++;
		    else if (entity.isOWLObjectProperty())
			objectPropertyAnnotation++;
		}
	    }
	}
    }

    public int countAnnotationAssertionAxiomsMetric(OWLOntology ontology, boolean withImports) {
	annotationAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.ANNOTATION_ASSERTION,
		OntologyUtility.ImportClosures(withImports));
	return annotationAssertionAxiomsCount;
    }

    public int countAnnotationPropertyDomainAxiomsMetric(OWLOntology ontology, boolean withImports) {
	annotationPropertyDomainAxiomsCount = ontology.getAxiomCount(AxiomType.ANNOTATION_PROPERTY_DOMAIN,
		OntologyUtility.ImportClosures(withImports));
	return annotationPropertyDomainAxiomsCount;
    }

    public int countAnnotationPropertyRangeAxiomsMetric(OWLOntology ontology, boolean withImports) {
	annotationPropertyRangeAxiomsCount = ontology.getAxiomCount(AxiomType.ANNOTATION_PROPERTY_RANGE,
		OntologyUtility.ImportClosures(withImports));
	return annotationPropertyRangeAxiomsCount;
    }
}
