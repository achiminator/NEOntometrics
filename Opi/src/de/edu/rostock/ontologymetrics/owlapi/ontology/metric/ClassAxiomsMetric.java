package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.metrics.GCICount;
import org.semanticweb.owlapi.metrics.HiddenGCICount;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class ClassAxiomsMetric implements Callable<ClassAxiomsMetric>{
    
    	private OWLOntology ontology;
    	public ClassAxiomsMetric(OWLOntology ontology) {
    	    this.ontology = ontology;
    	}
        private int subClassOfAxiomsCount;
        public int getSubClassOfAxiomsCount() {
	    return subClassOfAxiomsCount;
	}
	public int getEquivalentClassesAxiomsCount() {
	    return equivalentClassesAxiomsCount;
	}
	public int getDisjointClassesAxiomsCount() {
	    return disjointClassesAxiomsCount;
	}
	public int getGCICount() {
	    return GCICount;
	}
	public int getHiddenGCIcount() {
	    return hiddenGCIcount;
	}
	private int equivalentClassesAxiomsCount;
        private int disjointClassesAxiomsCount;
        private int GCICount;
        private int hiddenGCIcount;
    public Map<String, Object> getAllMetrics() {
  	Map<String, Object> returnObject = new LinkedHashMap<>();
  	returnObject.put("SubClassOfaxiomscount", subClassOfAxiomsCount);
  	returnObject.put("Equivalentclassesaxiomscount", equivalentClassesAxiomsCount);
  	returnObject.put("Disjointclassesaxiomscount", disjointClassesAxiomsCount);
  	returnObject.put("GCICount", GCICount);
  	returnObject.put("HiddenGCICount", hiddenGCIcount);
	return returnObject;
    }
    public ClassAxiomsMetric call() {
	
	countSubClassOfAxiomsMetric(ontology);
	countEquivalentClassesAxiomsMetric(ontology);
	countDisjointClassesAxiomsMetric(ontology);
	countGCIMetric(ontology);
	countHiddenGCIMetric(ontology);
	return this;
    }
    public int countSubClassOfAxiomsMetric(OWLOntology ontology) {
	subClassOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUBCLASS_OF, OntologyUtility.ImportClosures(true));
	return subClassOfAxiomsCount;
    }
    public int countEquivalentClassesAxiomsMetric(OWLOntology ontology) {
	equivalentClassesAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES, OntologyUtility.ImportClosures(true));
	return equivalentClassesAxiomsCount;
    }
    public int countDisjointClassesAxiomsMetric(OWLOntology ontology) {
	disjointClassesAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES, OntologyUtility.ImportClosures(true));
	return disjointClassesAxiomsCount;
    }
    public int countGCIMetric(OWLOntology ontology) {
	GCICount =  new GCICount(ontology).getValue();
	return GCICount;
    }
    public int countHiddenGCIMetric(OWLOntology ontology) {
	hiddenGCIcount = new HiddenGCICount(ontology).getValue();
	return hiddenGCIcount;
    }
    
}
