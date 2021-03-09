package de.edu.rostock.ontologymetrics.owlapi.ontology;

import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.OntologyMetric;

public interface OntologyMetrics {

	public OWLOntology getOntology();

	public void setOntology(OWLOntology pOntology);

	public void setIRI(IRI pIRI);

	// neu
	public void setIRI(String pIRI);

	public IRI getIRI();
	
	//neu
	public void resetIRI();

	/**
	 * get all metrics for the specified ontology, including:
	 * <ul>
	 * <li>base metrics</li>
	 * <li>schema metrics</li>
	 * <li>knowledgebase metrics</li>
	 * <li>class metrics</li>
	 * <li>graph metrics</li>
	 * </ul>
	 */
	public List<OntologyMetric> getAllMetrics();

	/**
	 * get all quality metrics for the specified ontology, including:
	 * <ul>
	 * <li>schema metrics</li>
	 * <li>knowledgebase metrics</li>
	 * <li>class metrics</li>
	 * <li>graph metrics</li>
	 * </ul>
	 */
	public List<OntologyMetric> getAllQualityMetrics();

	// Neue Kategorien f�r Base metrics
	public List<OntologyMetric> getBaseMetrics();

	public List<OntologyMetric> getClassAxiomsMetrics();

	public List<OntologyMetric> getObjectPropertyAxiomsMetric();

	public List<OntologyMetric> getDataPropertyAxiomsMetric();

	public List<OntologyMetric> getIndividualAxiomsMetric();

	public List<OntologyMetric> getAnnotationAxiomsMetricMetric();

	/**
	 * get all base metrics for the specified ontology.
	 */
	public List<OntologyMetric> getAllBaseMetrics();

	/**
	 * get all schema metrics for the specified ontology.
	 */
	public List<OntologyMetric> getAllSchemaMetrics();

	/**
	 * get all knowledgebase metrics for the specified ontology.
	 */
	public List<OntologyMetric> getAllKnowledgebaseMetrics();

	/**
	 * get all class metrics for the specified ontology.
	 */
	public List<OntologyMetric> getAllClassMetrics(IRI pIri);

	/**
	 * get all graph metrics for the specified ontology.
	 */
	public List<OntologyMetric> getAllGraphMetrics();

	/*
	 * base metrics
	 */
	public OntologyMetric getAnnotationAssertionAxiomsMetric();

	public OntologyMetric getAnnotationAxiomsMetric();

	public OntologyMetric getAnnotationPropertyDomainAxiomsMetric();

	public OntologyMetric getAnnotationPropertyRangeAxiomsMetric();

	public OntologyMetric getAsymmetricObjectPropertyAxiomsMetric();

	public OntologyMetric getAxiomsMetric();

	public OntologyMetric getClassAssertionAxiomsMetric();

	public OntologyMetric getClassesMetric();

	public OntologyMetric getDataPropertiesMetric();

	public OntologyMetric getDataPropertyAssertionAxiomsMetric();

	public OntologyMetric getDataPropertyDomainAxiomsMetric();

	public OntologyMetric getDataPropertyRangeAxiomsMetric();

	public OntologyMetric getDifferentIndividualsAxiomsMetric();

	public OntologyMetric getDisjointClassesAxiomsMetric();

	public OntologyMetric getDisjointDataPropertiesAxiomsMetric();

	public OntologyMetric getDisjointObjectPropertiesAxiomsMetric();

	public OntologyMetric getEquivalentClassesAxiomsMetric();

	public OntologyMetric getEquivalentDataPropertesAxiomsMetric();

	public OntologyMetric getEquivalentObjectPropertiesAxiomsMetric();

	public OntologyMetric getFunctionalDataPropertyAxiomsMetric();

	public OntologyMetric getFunctionalObjectPropertiesAxiomsMetric();

	public OntologyMetric getGCIMetric();

	public OntologyMetric getHiddenGCIMetric();

	public OntologyMetric getIndividualsMetric();

	public OntologyMetric getInverseFunctionalObjectPropertiesAxiomsMetric();

	public OntologyMetric getInverseObjectPropertiesAxiomsMetric();

	public OntologyMetric getIrreflexiveObjectPropertyAxiomsMetric();

	public OntologyMetric getLogicalAxiomsMetric();

	public OntologyMetric getNegativeDataPropertyAssertionAxiomsMetric();

	public OntologyMetric getNegativeObjectPropertyAssertionAxiomsMetric();

	public OntologyMetric getNumberOfClassesMetric();

	public OntologyMetric getNumberOfInstancesMetric();

	public OntologyMetric getNumberOfSubClassesMetric();

	public OntologyMetric getObjectPropertiesMetric();

	public OntologyMetric getObjectPropertyAssertionAxiomsMetric();

	public OntologyMetric getObjectPropertyDomainAxiomsMetric();

	public OntologyMetric getObjectPropertyRangeAxiomsMetric();

	public OntologyMetric getPropertiesMetric();

	public OntologyMetric getReflexiveObjectPropertyAxiomsMetric();

	public OntologyMetric getSameIndividualsAxiomsMetric();

	public OntologyMetric getSubClassOfAxiomsMetric();

	public OntologyMetric getSubDataPropertyOfAxiomsMetric();

	public OntologyMetric getSubObjectPropertyOfAxiomsMetric();

	public OntologyMetric getSubPropertyChainOfAxiomsMetric();

	public OntologyMetric getSymmetricObjectPropertyAxiomsMetric();

	public OntologyMetric getTotalClassesMetric();

	public OntologyMetric getTotalDataPropertiesMetric();

	public OntologyMetric getTotalIndividualsMetric();

	public OntologyMetric getTotalObjectPropertiesMetric();

	public OntologyMetric getTransitiveObjectPropertyAxiomsMetric();

	public OntologyMetric getDLExpressivityMetric(); // new MLIC

	/*
	 * schema metrics
	 */

	public OntologyMetric getAttributeRichnessMetric();

	public OntologyMetric getSchemaInheritenceRichness();

	public OntologyMetric getSchemaRelatioshipRichness();

	public OntologyMetric getAttributeClassRatio();

	// Added Metric
	public OntologyMetric getEquivalenceRatio();

	// Added Metric
	public OntologyMetric getAxiomClassRatio();
	
	// Added Metric
	//public OntologyMetric getIndividualClassRatio();

	// Added Metric
	public OntologyMetric getInverseRelationRatio();

	// Added Metric
	public OntologyMetric getClassRelationsRatio();
	// Added Metric

	/*
	 * knowledgebase metrics
	 */

	public OntologyMetric getAveragePopulationMetric();

	public OntologyMetric getClassRichnessMetric();

	/*public OntologyMetric getCohesionMetric();*/

	public OntologyMetric getInstanceCoverageMetric();

	public OntologyMetric getTreeBalanceMetric();

	/*
	 * class metrics
	 */

	public OntologyMetric getClassConnectivityMetric(IRI pIri);

	public OntologyMetric getClassFulnessMetric(IRI pIri);

	public OntologyMetric getClassImportanceMetric(IRI pIri);

	public OntologyMetric getClassInheritenceRichness(IRI pIri);

	public OntologyMetric getClassReadabilityMetric(IRI pIri);

	public OntologyMetric getClassRelationshipRichness(IRI pIri);

	public OntologyMetric getCountClassChildrenMetric(IRI pIri);

	public OntologyMetric getCountClassInstancesMetric(IRI pIri);

	public OntologyMetric getCountClassPropertiesMetric(IRI pIri);

	/*
	 * graph metrics
	 */
	public OntologyMetric getAverageDepthMetric();

	public OntologyMetric getAverageBreadthMetric();

	public OntologyMetric getTanglednessMetric();

	public OntologyMetric getDensityMetric();

	public OntologyMetric getLogicalAdaquacy();

	public OntologyMetric getModularityMetric();

	public OntologyMetric getAbsoluteLeafCardinalityMetric(); // new graph metric
	
	public OntologyMetric getRatioOfLeafFanOutness();
	
	public OntologyMetric getAbsoluteSiblingCardinalityMetric(); // new graph metric

	public OntologyMetric getRatioOfSiblingFanOutness();
	
	public OntologyMetric getMaximalDepthMetric(); // new graph metric

	public OntologyMetric getMaximalBreadthMetric(); // new graph metric
	
	public OntologyMetric getTotalNumberOfPathsMetric(); // new graph metric
	
	public OntologyMetric getAverageNumberOfPathsMetric(); // new graph metric
	
	public OntologyMetric getAbsoluteDepthMetric(); // new graph metric

	public OntologyMetric getAbsoluteBreadthMetric(); // new graph metric
	
	public OntologyMetric getAbsoluteRootCardinalityMetric(); // new graph metric

}
