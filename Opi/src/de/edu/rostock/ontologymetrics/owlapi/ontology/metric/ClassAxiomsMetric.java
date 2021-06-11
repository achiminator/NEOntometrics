package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.metrics.GCICount;
import org.semanticweb.owlapi.metrics.HiddenGCICount;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class ClassAxiomsMetric {
        
    public Map<String, Object> getAllMetrics(OWLOntology ontology) {
  	Map<String, Object> returnObject = new HashMap<>();
  	returnObject.put("SubClassOf axioms count", countSubClassOfAxiomsMetric(ontology));
  	returnObject.put("Equivalent classes axioms count", countEquivalentClassesAxiomsMetric(ontology));
  	returnObject.put("Disjoint classes axioms count", countDisjointClassesAxiomsMetric(ontology));
  	returnObject.put("GCICount", countGCIMetric(ontology));
  	returnObject.put("HiddenGCICount", countHiddenGCIMetric(ontology));
	return returnObject;
    }
    public int countSubClassOfAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.SUBCLASS_OF, OntologyUtility.ImportClosures(true));
    }
    public int countEquivalentClassesAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES, OntologyUtility.ImportClosures(true));
    }
    public int countDisjointClassesAxiomsMetric(OWLOntology ontology) {
	return ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES, OntologyUtility.ImportClosures(true));
    }
    public int countGCIMetric(OWLOntology ontology) {
	return (new GCICount(ontology).getValue());
    }
    public int countHiddenGCIMetric(OWLOntology ontology) {
	return (new HiddenGCICount(ontology).getValue());
    }
    
}
