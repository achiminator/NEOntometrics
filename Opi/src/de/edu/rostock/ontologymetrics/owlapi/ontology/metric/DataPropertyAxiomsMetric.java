package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class DataPropertyAxiomsMetric implements Callable<DataPropertyAxiomsMetric> {

    private boolean withImports;
    private int subDataPropertyOfAxiomsCount;
    private int equivalentDataPropertyAxiomsCount;
    private int disjointDataPropertyAxiomsCount;
    private int functionalDataPropertyAxiomsCount;
    private int dataPropertyDomainAxiomsMetric;
    private int dataPropertyRangeAxiomsMetric;
    private OWLOntology ontology;
    public DataPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	this.ontology = ontology;
	this.withImports = withImports;
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
	
	countSubDataPropertyOfAxiomsMetric(ontology, withImports);
	countEquivalentDataPropertyAxiomsMetric(ontology, withImports);
	countDisjointDataPropertyAxiomsMetric(ontology,withImports);
	countFunctionalDataPropertyAxiomsMetric(ontology,withImports);
	countDataPropertyDomainAxiomsMetric(ontology,withImports);
	countDataPropertyRangeAxiomsMetric(ontology,withImports);
	return this;
    }

    public int countSubDataPropertyOfAxiomsMetric(OWLOntology ontology, boolean withImports) {
	subDataPropertyOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_DATA_PROPERTY, OntologyUtility.ImportClosures(withImports));
	return subDataPropertyOfAxiomsCount;
    }

    public int countEquivalentDataPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	equivalentDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_DATA_PROPERTIES, OntologyUtility.ImportClosures(withImports));
	return equivalentDataPropertyAxiomsCount;
    }

    public int countDisjointDataPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	disjointDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_DATA_PROPERTIES, OntologyUtility.ImportClosures(withImports));
	return disjointDataPropertyAxiomsCount;
    }

    public int countFunctionalDataPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	functionalDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_DATA_PROPERTY, OntologyUtility.ImportClosures(withImports));
	return functionalDataPropertyAxiomsCount;
    }

    public int countDataPropertyDomainAxiomsMetric(OWLOntology ontology, boolean withImports) {
	dataPropertyDomainAxiomsMetric = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_DOMAIN, OntologyUtility.ImportClosures(withImports));
	return dataPropertyDomainAxiomsMetric;
    }
    public int countDataPropertyRangeAxiomsMetric(OWLOntology ontology, boolean withImports) {
	dataPropertyRangeAxiomsMetric = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_RANGE, OntologyUtility.ImportClosures(withImports));
	return dataPropertyRangeAxiomsMetric;
    }
}
