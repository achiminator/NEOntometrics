package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;


import java.util.concurrent.Callable;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class DataPropertyAxiomsMetric extends MetricCalculations implements Callable<DataPropertyAxiomsMetric> {
    public DataPropertyAxiomsMetric(OWLOntology ontology, boolean withImports) {
	super(ontology, withImports);
    }


    public DataPropertyAxiomsMetric call() {
	countSubDataPropertyOfAxiomsMetric();
	countEquivalentDataPropertyAxiomsMetric();
	countDisjointDataPropertyAxiomsMetric();
	countFunctionalDataPropertyAxiomsMetric();
	countDataPropertyDomainAxiomsMetric();
	countDataPropertyRangeAxiomsMetric();
	return this;
    }

    public void countSubDataPropertyOfAxiomsMetric() {
	int subDataPropertyOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_DATA_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("SubDataPropertyOfaxiomscount", subDataPropertyOfAxiomsCount);

    }

    public void iterativeDataPropertyMetrics() {
//	DataProperties = ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(withImports))
    }

    public void countEquivalentDataPropertyAxiomsMetric() {
	int equivalentDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_DATA_PROPERTIES,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Equivalentdatapropertiesaxiomscount", equivalentDataPropertyAxiomsCount);

    }

    public void countDisjointDataPropertyAxiomsMetric() {
	int disjointDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_DATA_PROPERTIES,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Disjointdatapropertiesaxiomscount", disjointDataPropertyAxiomsCount);
    }

    public void countFunctionalDataPropertyAxiomsMetric() {
	int functionalDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_DATA_PROPERTY,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Functionaldatapropertyaxiomscount", functionalDataPropertyAxiomsCount);
    }

    public void countDataPropertyDomainAxiomsMetric() {
	int dataPropertyDomainAxiomsMetric = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_DOMAIN,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Datapropertydomainaxiomscount", dataPropertyDomainAxiomsMetric);
    }

    public void countDataPropertyRangeAxiomsMetric() {
	int dataPropertyRangeAxiomsMetric = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_RANGE,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("DataPropertyrangeaxiomscount", dataPropertyRangeAxiomsMetric);
    }
}
