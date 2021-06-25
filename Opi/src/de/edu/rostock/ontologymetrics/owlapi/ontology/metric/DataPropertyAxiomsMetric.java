package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class DataPropertyAxiomsMetric {

    private int subDataPropertyOfAxiomsCount;
    private int equivalentDataPropertyAxiomsCount;
    private int disjointDataPropertyAxiomsCount;
    private int functionalDataPropertyAxiomsCount;
    private int dataPropertyDomainAxiomsMetric;
    private int dataPropertyRangeAxiomsMetric;
    public Map<String, Object> getAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("SubDataPropertyOfaxiomscount", subDataPropertyOfAxiomsCount);
	returnObject.put("Equivalentdatapropertiesaxiomscount", equivalentDataPropertyAxiomsCount);
	returnObject.put("Disjointdatapropertiesaxiomscount", disjointDataPropertyAxiomsCount);
	returnObject.put("Functionaldatapropertyaxiomscount", functionalDataPropertyAxiomsCount);
	returnObject.put("Datapropertydomainaxiomscount", dataPropertyDomainAxiomsMetric);
	returnObject.put("DataPropertyrangeaxiomscount", dataPropertyRangeAxiomsMetric);
	return returnObject;
    }
    public Map<String, Object> calculateAllMetrics(OWLOntology ontology) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("SubDataPropertyOfaxiomscount", countSubDataPropertyOfAxiomsMetric(ontology));
	returnObject.put("Equivalentdatapropertiesaxiomscount", countEquivalentDataPropertyAxiomsMetric(ontology));
	returnObject.put("Disjointdatapropertiesaxiomscount", countDisjointDataPropertyAxiomsMetric(ontology));
	returnObject.put("Functionaldatapropertyaxiomscount", countFunctionalDataPropertyAxiomsMetric(ontology));
	returnObject.put("Datapropertydomainaxiomscount", countDataPropertyDomainAxiomsMetric(ontology));
	returnObject.put("DataPropertyrangeaxiomscount", countDataPropertyRangeAxiomsMetric(ontology));
	return returnObject;
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
