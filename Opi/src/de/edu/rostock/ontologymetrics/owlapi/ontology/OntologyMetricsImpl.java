package de.edu.rostock.ontologymetrics.owlapi.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.BaseMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.OntologyMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.GraphParser;

/**
 * This class represents the implementation of the interface
 * <code>OntologyMetrics</code>.
 * 
 */
public class OntologyMetricsImpl  {

    private OWLOntology ontology;
    protected GraphParser parser;
    protected GraphParser parserI;

    private IRI iri;

    public OntologyMetricsImpl(OWLOntology pOntology) {
	if (pOntology != null) {
	    ontology = pOntology;
	    parser = new GraphParser(ontology, false); // without imports
	    parserI = new GraphParser(ontology, true); // with imports
	} else {
	    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	    try {
		ontology = manager.createOntology();
		parser = new GraphParser(ontology, false); // without imports
		parserI = new GraphParser(ontology, true); // with imports
	    } catch (OWLOntologyCreationException e) {
		System.out.println(e.getMessage());
		e.printStackTrace();
	    }
	}
	ontology = pOntology;
	iri = null;
    }

    /*
     * getter $ setter
     */

    public OWLOntology getOntology() {
	return ontology;
    }

    public void setOntology(OWLOntology pOntology) {
	ontology = pOntology;
    }

    public void setIRI(IRI pIRI) {
	iri = pIRI;
    }

    // neu
    public void setIRI(String pIRI) {
	iri = IRI.create(pIRI);
    }

    public IRI getIRI() {
	return iri;
    }

    // neu
    public void resetIRI() {
	iri = null;
    }

    /*
     * metrics
     */
    public Map<String, Object> getAllMetrics() {
	Map<String, Object> metrics = new HashMap<String, Object>();
	metrics.put("Base Metrics", getAllBaseMetrics());
	metrics.addAll(getAllSchemaMetrics());
	metrics.addAll(getAllKnowledgebaseMetrics());
	metrics.addAll(getAllClassMetrics(getIRI()));
	metrics.addAll(getAllGraphMetrics());

	return metrics;
    }

    public List<OntologyMetric> getAllQualityMetrics() {
	List<OntologyMetric> metrics = new ArrayList<OntologyMetric>();
	metrics.addAll(getAllSchemaMetrics());
	metrics.addAll(getAllKnowledgebaseMetrics());
	metrics.addAll(getAllClassMetrics(getIRI()));
	metrics.addAll(getAllGraphMetrics());
	return metrics;
    }



    /*
     * class metrics
     */
    public List<OntologyMetric> getAllClassMetrics(IRI pIri) {
	List<OntologyMetric> classMetrics = new ArrayList<OntologyMetric>();

	// ohne �bergabe schauen auf globale Varibale
	if (pIri == null)
	    pIri = getIRI();

//	classMetrics.add(getClassConnectivityMetric(pIri));
//	classMetrics.add(getClassFulnessMetric(pIri));
//	classMetrics.add(getClassImportanceMetric(pIri));
//	classMetrics.add(getClassInheritenceRichness(pIri));
//	classMetrics.add(getClassReadabilityMetric(pIri));
//	classMetrics.add(getClassRelationshipRichness(pIri));
//	classMetrics.add(getCountClassChildrenMetric(pIri));
	classMetrics.add(getCountClassInstancesMetric(pIri));
	classMetrics.add(getCountClassPropertiesMetric(pIri));

	return classMetrics;
    }

    /*
     * base metrics functions not grouped
     */
   

    public OntologyMetric getLogicalAxiomsMetric() {
	return new CountLogicalAxiomsMetric(ontology);
    }

    public OntologyMetric getNegativeDataPropertyAssertionAxiomsMetric() {
	return new CountNegativeDataPropertyAssertionAxiomsMetric(ontology);
    }

    public OntologyMetric getNegativeObjectPropertyAssertionAxiomsMetric() {
	return new CountNegativeObjectPropertyAssertionAxiomsMetric(ontology);
    }

    public OntologyMetric getNumberOfClassesMetric() {
	return new CountNumberOfClassesMetric(ontology);
    }

    public OntologyMetric getNumberOfInstancesMetric() {
	return new CountNumberOfInstancesMetric(ontology);
    }

    public OntologyMetric getNumberOfSubClassesMetric() {
	return new CountNumberOfSubClassesMetric(ontology);
    }

    public OntologyMetric getObjectPropertiesMetric() {
	return new CountObjectPropertiesMetric(ontology);
    }

    public OntologyMetric getObjectPropertyAssertionAxiomsMetric() {
	return new CountObjectPropertyAssertionAxiomsMetric(ontology);
    }

    public OntologyMetric getObjectPropertyDomainAxiomsMetric() {
	return new CountObjectPropertyDomainAxiomsMetric(ontology);
    }

    public OntologyMetric getObjectPropertyRangeAxiomsMetric() {
	return new CountObjectPropertyRangeAxiomsMetric(ontology);
    }

    public OntologyMetric getPropertiesMetric() {
	return new CountPropertiesMetric(ontology);
    }

    public OntologyMetric getReflexiveObjectPropertyAxiomsMetric() {
	return new CountReflexiveObjectPropertyAxiomsMetric(ontology);
    }

    public OntologyMetric getSameIndividualsAxiomsMetric() {
	return new CountSameIndividualsAxiomsMetric(ontology);
    }

    public OntologyMetric getSubClassOfAxiomsMetric() {
	return new CountSubClassOfAxiomsMetric(ontology);
    }

    public OntologyMetric getSubDataPropertyOfAxiomsMetric() {
	return new CountSubDataPropertyOfAxiomsMetric(ontology);
    }

    public OntologyMetric getSubObjectPropertyOfAxiomsMetric() {
	return new CountSubObjectPropertyOfAxiomsMetric(ontology);
    }

    public OntologyMetric getSubPropertyChainOfAxiomsMetric() {
	return new CountSubPropertyChainOfAxiomsMetric(ontology);
    }

    public OntologyMetric getSymmetricObjectPropertyAxiomsMetric() {
	return new CountSymmetricObjectPropertyAxiomsMetric(ontology);
    }


    


    public OntologyMetric getTransitiveObjectPropertyAxiomsMetric() {
	return new CountTransitiveObjectPropertyAxiomsMetric(ontology);
    }

    // new MLI
    /*
     * graph metrics
     */
    public OntologyMetric getAverageDepthMetric() {
	return new AverageDepthMetric(ontology);
    }

    public OntologyMetric getAverageBreadthMetric() {
	return new AverageBreadthMetric(ontology);
    }

    public OntologyMetric getMaximalDepthMetric() { // new graph metric
	return new MaximalDepthMetric(ontology);
    }

    public OntologyMetric getMaximalBreadthMetric() { // new graph metric
	return new MaximalBreadthMetric(ontology);
    }

    public OntologyMetric getAbsoluteDepthMetric() { // new graph metric
	return new AbsoluteDepthMetric(ontology);
    }

    public OntologyMetric getAbsoluteBreadthMetric() { // new graph metric
	return new AbsoluteBreadthMetric(ontology);
    }

    public OntologyMetric getTotalNumberOfPathsMetric() { // new graph metric
	return new TotalNumberOfPathsMetric(ontology);
    }


    public OntologyMetric getAbsoluteLeafCardinalityMetric() { // new graph
							       // metric
	return new AbsoluteLeafCardinalityMetric(ontology);
    }

    public OntologyMetric getAbsoluteSiblingCardinalityMetric() { // new graph
	// metric
	return new AbsoluteSiblingCardinalityMetric(ontology);
    }

    public OntologyMetric getAbsoluteRootCardinalityMetric() { // new graph
	// metric
	return new AbsoluteRootCardinalityMetric(ontology);
    }

    public OntologyMetric getTanglednessMetric() {
	return new TanglednessMetric(ontology);
    }

    public OntologyMetric getDensityMetric() {
	return new DensityMetric(ontology);
    }

    public OntologyMetric getLogicalAdaquacy() {
	return new LogicalAdequacyMetric(ontology);
    }

    public OntologyMetric getModularityMetric() {
	return new ModularityMetric(ontology);
    }

    public OntologyMetric getRatioOfLeafFanOutness() {
	return new RatioOfLeafFanOutnessMetric(ontology);
    }

    public OntologyMetric getRatioOfSiblingFanOutness() {
	return new RatioOfSiblingFanOutnessMetric(ontology);
    }

    /*
     * schema metrics
     */
    public OntologyMetric getAttributeRichnessMetric() {
	return new AttributeRichnessMetric(ontology);
    }

    public OntologyMetric getSchemaInheritenceRichness() {
	return new SchemaInheritenceRichnessMetric(ontology);
    }

    public OntologyMetric getSchemaRelatioshipRichness() {
	return new SchemaRelationshipRichnessMetric(ontology);
    }

    public OntologyMetric getAttributeClassRatio() {
	return new AttributeClassRatio(ontology);
    }
    // New

    public OntologyMetric getEquivalenceRatio() {
	return new EquivalenceRatioMetric(ontology);
    }
    // New

    public OntologyMetric getAxiomClassRatio() {
	return new AxiomClassRatioMetric(ontology);
    }

    /*
     * public OntologyMetric getIndividualClassRatio() { return new
     * IndividualClassRatioMetric(ontology); }
     */
    // New

    public OntologyMetric getInverseRelationRatio() {
	return new InverseRelationsRatioMetric(ontology);
    }
    // New

    public OntologyMetric getClassRelationsRatio() {
	return new ClassRelationsRatioMetric(ontology);
    }
    // New

    /*
     * knowledgebase metrics
     */
    public OntologyMetric getAveragePopulationMetric() {
	return new AveragePopulationMetric(ontology);
    }

    public OntologyMetric getClassRichnessMetric() {
	return new ClassRichnessMetric(ontology);
    }

    /*
     * public OntologyMetric getCohesionMetric() { return new
     * CohesionMetric(ontology); }
     */

    public OntologyMetric getInstanceCoverageMetric() {
	return new InstanceCoverageMetric(ontology);
    }

    public OntologyMetric getTreeBalanceMetric() {
	return new TreeBalanceMetric(ontology);
    }

    /*
     * class metrics
     */
    public OntologyMetric getClassConnectivityMetric(IRI pIri) {
	return new ClassConnectivityMetric(ontology, pIri);
    }

    public OntologyMetric getClassFulnessMetric(IRI pIri) {
	return new ClassFulnessMetric(ontology, pIri);
    }

    public OntologyMetric getClassImportanceMetric(IRI pIri) {
	return new ClassImportanceMetric(ontology, pIri);
    }

    public OntologyMetric getClassInheritenceRichness(IRI pIri) {
	return new ClassInheritenceRichnessMetric(ontology, pIri);
    }

    public OntologyMetric getClassReadabilityMetric(IRI pIri) {
	return new ClassReadabilityMetric(ontology, pIri);
    }

    public OntologyMetric getClassRelationshipRichness(IRI pIri) {
	return new ClassRelationshipRichnessMetric(ontology, pIri);
    }

    public OntologyMetric getCountClassChildrenMetric(IRI pIri) {
	return new CountClassChildrenMetric(ontology, pIri);
    }

    public OntologyMetric getCountClassInstancesMetric(IRI pIri) {
	return new CountClassInstancesMetric(ontology, pIri);
    }

    public OntologyMetric getCountClassPropertiesMetric(IRI pIri) {
	return new CountClassPropertiesMetric(ontology, pIri);
    }
}
