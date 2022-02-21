package main.java.neontometrics.calc;

import java.util.Map;
import java.util.concurrent.Callable;
import org.semanticweb.owlapi.metrics.GCICount;
import org.semanticweb.owlapi.metrics.HiddenGCICount;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

import main.java.neontometrics.calc.handler.OntologyUtility;

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

    private void countSubClassOfAxiomsMetric() {
	int subClassOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUBCLASS_OF,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("subClassOfAxioms", subClassOfAxiomsCount);

    }

    private void countEquivalentClassesAxiomsMetric() {
	int equivalentClassesAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("equivalentClassesAxioms", equivalentClassesAxiomsCount);

    }

    private void countDisjointClassesAxiomsMetric() {
	int disjointClassesAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES,
		OntologyUtility.ImportClosures(imports));
	returnObject.put("disjointClassesAxioms", disjointClassesAxiomsCount);

    }

    private void countGCIMetric() {
	GCICount gc = new GCICount(ontology);
	gc.setImportsClosureUsed(imports);

	returnObject.put("gciCount", gc.getValue());

    }

    private void countHiddenGCIMetric() {
	HiddenGCICount hgc = new HiddenGCICount(ontology);
	hgc.setImportsClosureUsed(imports);
	returnObject.put("hiddengciCount", hgc.getValue());

    }

}
