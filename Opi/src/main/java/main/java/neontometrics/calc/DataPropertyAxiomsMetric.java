package main.java.neontometrics.calc;

import java.util.concurrent.Callable;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

import main.java.neontometrics.calc.handler.OntologyUtility;

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

	/**
	 * Count the "SubDataProperty Axioms, which is used to structure Data Properties
	 */
	private void countSubDataPropertyOfAxiomsMetric() {
		int subDataPropertyOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_DATA_PROPERTY,
				OntologyUtility.ImportClosures(imports));
		returnObject.put("subDataPropertyOfAxioms", subDataPropertyOfAxiomsCount);

	}

	private void iterativeDataPropertyMetrics() {
//	DataProperties = ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(withImports))
	}

	/**
	 * Count the equivalence axioms for data properties
	 */
	private void countEquivalentDataPropertyAxiomsMetric() {
		int equivalentDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_DATA_PROPERTIES,
				OntologyUtility.ImportClosures(imports));
		returnObject.put("equivalentDataPropertiesAxioms", equivalentDataPropertyAxiomsCount);

	}

	private void countDisjointDataPropertyAxiomsMetric() {
		int disjointDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_DATA_PROPERTIES,
				OntologyUtility.ImportClosures(imports));
		returnObject.put("disjointDataPropertiesAxioms", disjointDataPropertyAxiomsCount);
	}

	private void countFunctionalDataPropertyAxiomsMetric() {
		int functionalDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_DATA_PROPERTY,
				OntologyUtility.ImportClosures(imports));
		returnObject.put("functionalDataPropertyAxioms", functionalDataPropertyAxiomsCount);
	}

	private void countDataPropertyDomainAxiomsMetric() {
		int dataPropertyDomainAxiomsMetric = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_DOMAIN,
				OntologyUtility.ImportClosures(imports));
		returnObject.put("dataPropertyDomainAxioms", dataPropertyDomainAxiomsMetric);
	}

	private void countDataPropertyRangeAxiomsMetric() {
		int dataPropertyRangeAxiomsMetric = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_RANGE,
				OntologyUtility.ImportClosures(imports));
		returnObject.put("dataPropertyRangeAxioms", dataPropertyRangeAxiomsMetric);
	}
}
