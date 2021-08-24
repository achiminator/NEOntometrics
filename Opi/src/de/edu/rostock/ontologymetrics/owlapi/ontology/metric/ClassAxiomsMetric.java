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
    	private boolean withImports;
    	public ClassAxiomsMetric(OWLOntology ontology, boolean withImports) {
    	    this.ontology = ontology;
    	    this.withImports = withImports;
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
	
	countSubClassOfAxiomsMetric(ontology, withImports);
	countEquivalentClassesAxiomsMetric(ontology, withImports);
	countDisjointClassesAxiomsMetric(ontology, withImports);
	countGCIMetric(ontology, withImports);
	countHiddenGCIMetric(ontology, withImports);
	return this;
    }
    public int countSubClassOfAxiomsMetric(OWLOntology ontology, boolean withImports) {
	subClassOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUBCLASS_OF, OntologyUtility.ImportClosures(withImports));
	return subClassOfAxiomsCount;
    }
    public int countEquivalentClassesAxiomsMetric(OWLOntology ontology, boolean withImports) {
	equivalentClassesAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES, OntologyUtility.ImportClosures(withImports));
	return equivalentClassesAxiomsCount;
    }
    public int countDisjointClassesAxiomsMetric(OWLOntology ontology, boolean withImports) {
	disjointClassesAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES, OntologyUtility.ImportClosures(withImports));
	return disjointClassesAxiomsCount;
    }
    public int countGCIMetric(OWLOntology ontology, boolean withImports) {
	GCICount gc = new GCICount(ontology);
	gc.setImportsClosureUsed(withImports);
	GCICount =  gc.getValue();
	return GCICount;
    }
    public int countHiddenGCIMetric(OWLOntology ontology, boolean withImports) {
	HiddenGCICount hgc =new HiddenGCICount(ontology);
	hgc.setImportsClosureUsed(withImports);
	hiddenGCIcount = hgc.getValue();
	return hiddenGCIcount;
    }
    
}
