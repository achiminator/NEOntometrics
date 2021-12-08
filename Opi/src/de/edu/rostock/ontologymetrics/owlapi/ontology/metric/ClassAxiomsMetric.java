package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.Map;
import java.util.concurrent.Callable;
import org.semanticweb.owlapi.metrics.GCICount;
import org.semanticweb.owlapi.metrics.HiddenGCICount;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class ClassAxiomsMetric extends MetricCalculations implements Callable<ClassAxiomsMetric> {

    public ClassAxiomsMetric(OWLOntology ontology, boolean withImports) {
	super(ontology, withImports);
    }

    public Map<String, Object> getAllMetrics() {
	return returnObject;
    }

    public ClassAxiomsMetric call() {

	countSubClassOfAxiomsMetric();
	countEquivalentClassesAxiomsMetric();
	countDisjointClassesAxiomsMetric();
	countGCIMetric();
	countHiddenGCIMetric();
	return this;
    }

    public void countSubClassOfAxiomsMetric() {
	int subClassOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUBCLASS_OF,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("SubClassOfaxiomscount", subClassOfAxiomsCount);

    }

    public void countEquivalentClassesAxiomsMetric() {
	int equivalentClassesAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Equivalentclassesaxiomscount", equivalentClassesAxiomsCount);

    }

    public void countDisjointClassesAxiomsMetric() {
	int disjointClassesAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("Disjointclassesaxiomscount", disjointClassesAxiomsCount);

    }

    public void countGCIMetric() {
	GCICount gc = new GCICount(ontology);
	gc.setImportsClosureUsed(imports);

	returnObject.put("GCICount", gc.getValue());

    }

    public void countHiddenGCIMetric() {
	HiddenGCICount hgc = new HiddenGCICount(ontology);
	hgc.setImportsClosureUsed(imports);
	returnObject.put("HiddenGCICount", hgc.getValue());

    }

}
