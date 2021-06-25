package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class DataPropertyAxiomsMetric implements Callable<DataPropertyAxiomsMetric> {

    private int subDataPropertyOfAxiomsCount;
    private int equivalentDataPropertyAxiomsCount;
    private int disjointDataPropertyAxiomsCount;
    private int functionalDataPropertyAxiomsCount;
    private int dataPropertyDomainAxiomsMetric;
    private int dataPropertyRangeAxiomsMetric;
    private OWLOntology ontology;
    public DataPropertyAxiomsMetric(OWLOntology ontology) {
	this.ontology = ontology;
    }
    
    public Map<String, Object> getAllMetrics() {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("SubDataPropertyOfaxiomscount", subDataPropertyOfAxiomsCount);
	returnObject.put("Equivalentdatapropertiesaxiomscount", equivalentDataPropertyAxiomsCount);
	returnObject.put("Disjointdatapropertiesaxiomscount", disjointDataPropertyAxiomsCount);
	returnObject.put("Functionaldatapropertyaxiomscount", functionalDataPropertyAxiomsCount);
	returnObject.put("Datapropertydomainaxiomscount", dataPropertyDomainAxiomsMetric);
	returnObject.put("DataPropertyrangeaxiomscount", dataPropertyRangeAxiomsMetric);
	return returnObject;
    }
    public DataPropertyAxiomsMetric call() {
	
	countSubDataPropertyOfAxiomsMetric(ontology);
	countEquivalentDataPropertyAxiomsMetric(ontology);
	countDisjointDataPropertyAxiomsMetric(ontology);
	countFunctionalDataPropertyAxiomsMetric(ontology);
	countDataPropertyDomainAxiomsMetric(ontology);
	countDataPropertyRangeAxiomsMetric(ontology);
	return this;
    }

    public int countSubDataPropertyOfAxiomsMetric(OWLOntology ontology) {
	subDataPropertyOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_DATA_PROPERTY, OntologyUtility.ImportClosures(true));
	return subDataPropertyOfAxiomsCount;
    }

    public int countEquivalentDataPropertyAxiomsMetric(OWLOntology ontology) {
	equivalentDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_DATA_PROPERTIES, OntologyUtility.ImportClosures(true));
	return equivalentDataPropertyAxiomsCount;
    }

    public int countDisjointDataPropertyAxiomsMetric(OWLOntology ontology) {
	disjointDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_DATA_PROPERTIES, OntologyUtility.ImportClosures(true));
	return disjointDataPropertyAxiomsCount;
    }

    public int countFunctionalDataPropertyAxiomsMetric(OWLOntology ontology) {
	functionalDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_DATA_PROPERTY, OntologyUtility.ImportClosures(true));
	return functionalDataPropertyAxiomsCount;
    }

    public int countDataPropertyDomainAxiomsMetric(OWLOntology ontology) {
	dataPropertyDomainAxiomsMetric = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_DOMAIN, OntologyUtility.ImportClosures(true));
	return dataPropertyDomainAxiomsMetric;
    }
    public int countDataPropertyRangeAxiomsMetric(OWLOntology ontology) {
	dataPropertyRangeAxiomsMetric = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_RANGE, OntologyUtility.ImportClosures(true));
	return dataPropertyRangeAxiomsMetric;
    }
}
