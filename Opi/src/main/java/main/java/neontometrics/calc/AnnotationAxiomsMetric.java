package main.java.neontometrics.calc;

import java.util.Set;
import java.util.concurrent.Callable;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import main.java.neontometrics.calc.handler.OntologyUtility;

public class AnnotationAxiomsMetric extends MetricCalculations implements Callable<AnnotationAxiomsMetric> {

    /**
     * Counts the number of Annotations
     * 
     * @param ontology
     * @param withImports States whether you want to consider Imports as well or not
     */
    public AnnotationAxiomsMetric(OWLOntology ontology, boolean withImports) {
	super(ontology, withImports);
    }

    public AnnotationAxiomsMetric call() {
	countAnnotationAxiomsMetric();
	countAnnotationAssertionAxiomsMetric();
	countAnnotationPropertyDomainAxiomsMetric();
	countAnnotationPropertyRangeAxiomsMetric();
	iterativeAnnotationMetrics();
	return this;
    }

    /**
     * Counts the Annotations that describe the Ontology in general (not Annotations
     * per class)
     * 
     * @param ontology
     * @return
     */
    private void countAnnotationAxiomsMetric() {
	int annotationAxiomsCount = ontology.getAnnotations().size();
	returnObject.put("generalAnnotationAxioms", annotationAxiomsCount);
    }

    /**
     * Some of the Annotationmetrics (the count of the detailed property and class
     * annotations)need to be run by iteration over the elements. These are done
     * then done all at once.
     * 
     * @param withImports
     */
    private void iterativeAnnotationMetrics() {
	int objectPropertyAnnotation = 0;
	int dataPropertyAnnotation = 0;
	int datatypeAnnotation = 0;
	int individualAnnotation = 0;
	int classAnnoation = 0;
	int deprecatedClass = 0;
	int deprecatedDataType = 0;
	int deprecatedDataProperty = 0;
	int deprecatedIndividual = 0;
	int deprecatedObjectProperty = 0;
	Set<OWLAnnotationAssertionAxiom> annotationAxioms = ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION,
		OntologyUtility.ImportClosures(imports));
	for (OWLAnnotationAssertionAxiom owlAnnotationAssertionAxiom : annotationAxioms) {

	    if (!(owlAnnotationAssertionAxiom.getSubject() instanceof OWLAnonymousIndividual)) {
		Set<OWLEntity> entities = ontology
			.getEntitiesInSignature((IRI) owlAnnotationAssertionAxiom.getSubject());
		for (OWLEntity entity : entities) {
		    if (entity.isOWLClass()) {
			if (owlAnnotationAssertionAxiom.isDeprecatedIRIAssertion())
			    deprecatedClass++;
			classAnnoation++;
		    } else if (entity.isOWLDatatype()) {
			if (owlAnnotationAssertionAxiom.isDeprecatedIRIAssertion())
			    deprecatedDataType++;
			datatypeAnnotation++;}
			else if (entity.isOWLNamedIndividual()) {
			    if (owlAnnotationAssertionAxiom.isDeprecatedIRIAssertion())
				deprecatedIndividual++;
			    individualAnnotation++;
			
		    } else if (entity.isOWLDataProperty()) {
			if (owlAnnotationAssertionAxiom.isDeprecatedIRIAssertion())
			    deprecatedDataProperty++;
			dataPropertyAnnotation++;
		    } else if (entity.isOWLObjectProperty()) {
			if (owlAnnotationAssertionAxiom.isDeprecatedIRIAssertion())
			    deprecatedObjectProperty++;
			objectPropertyAnnotation++;
		    }
		}
	    }
	}

	returnObject.put("dataPropertyAnnotations", dataPropertyAnnotation);
	returnObject.put("classAnnotations", classAnnoation);
	returnObject.put("objectPropertyAnnotations", objectPropertyAnnotation);
	returnObject.put("individualAnnotations", individualAnnotation);
	returnObject.put("datatypeAnnotations", datatypeAnnotation);
	returnObject.put("deprecatedDataProperties", deprecatedDataProperty);
	returnObject.put("deprecatedClasses", deprecatedClass);
	returnObject.put("deprecatedObjectProperties", deprecatedObjectProperty);
	returnObject.put("deprecatedDataTypes", deprecatedDataType);
	returnObject.put("deprecatedIndividuals", deprecatedIndividual);
    }

    private void countAnnotationAssertionAxiomsMetric() {
	int annotationAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.ANNOTATION_ASSERTION,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("annotationAssertionsAxioms", annotationAssertionAxiomsCount);

    }

    private void countAnnotationPropertyDomainAxiomsMetric() {
	int annotationPropertyDomainAxiomsCount = ontology.getAxiomCount(AxiomType.ANNOTATION_PROPERTY_DOMAIN,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("annotationPropertyDomainAxioms", annotationPropertyDomainAxiomsCount);
    }

    private void countAnnotationPropertyRangeAxiomsMetric() {
	int annotationPropertyRangeAxiomsCount = ontology.getAxiomCount(AxiomType.ANNOTATION_PROPERTY_RANGE,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("annotationPropertyRangeaxioms", annotationPropertyRangeAxiomsCount);
    }
}
