package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class RatioOfLeafFanOutnessMetric extends GraphMetric {

	public RatioOfLeafFanOutnessMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Double getValue() {
		// Set<OWLClass> leaves = new
		// GraphBaseLeavesMetric(ontology).getValue();
		// Integer totalClasses = new
		// CountTotalClassesMetric(ontology).getValue();
		// return (float) leaves.size() / (float) totalClasses;

		double leaves = (double) parser.getLeaveClasses().size();
		double noclassesImp = (double) parserI.getNoClasses(); //with imports

		if (noclassesImp > 0.0)
			return OntologyUtility.roundByGlobNK(leaves / noclassesImp);
		else
			return 0.0;
	}

	@Override
	public String toString() {
		return "Ratio of leaf fan-outness";
	}
}
