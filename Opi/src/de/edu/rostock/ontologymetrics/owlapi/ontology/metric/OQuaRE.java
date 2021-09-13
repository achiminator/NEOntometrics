package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.jena.atlas.iterator.IteratorWithHistory;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class OQuaRE implements Callable<OQuaRE> {
    private double LCOMOnto;
    private double WMCOnto;
    private double DITOnto;
    private double NACOnto;
    private double NOCOnto;
    private double CBOOnto;
    private double RFCOnto;
    private double NOMOnto;
    private double RROnto;
    private double AROnto;
    private double INROnto;
    private double CRnto;
    private double ANOnto;
    private double TMOnto;

    private OWLOntology ontology;
    private boolean withImports;

    /**
     * Counts the number of Annotations
     * 
     * @param ontology
     * @param withImports States whether you want to consider Imports as well or not
     */
    public OQuaRE(OWLOntology ontology, boolean withImports) {
	this.ontology = ontology;
	this.withImports = withImports;
    }

    public Map<String, Object> getAllMetrics() {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("Annotationaxiomscount", annotationAxiomsCount);
	returnObject.put("Annotationassertionaxiomscount", annotationAssertionAxiomsCount);
	returnObject.put("Annotationpropertydomainaxiomscount", annotationPropertyDomainAxiomsCount);
	returnObject.put("Annotationpropertyrangeaxiomscount", annotationPropertyRangeAxiomsCount);
	return returnObject;
    }

    public OQuaRE call() {

	countAnnotationAxiomsMetric(ontology);
	countAnnotationAssertionAxiomsMetric(ontology, withImports);
	countAnnotationPropertyDomainAxiomsMetric(ontology, withImports);
	countAnnotationPropertyRangeAxiomsMetric(ontology, withImports);
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
