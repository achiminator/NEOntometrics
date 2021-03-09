package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class RatioOfSiblingFanOutnessMetric extends GraphMetric {

	public RatioOfSiblingFanOutnessMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Double getValue() {
		// int countSiblings = 0;
		// Set<Set<OWLClass>> siblings = new GraphBaseSiblingsMetric(ontology)
		// .getValue();
		// Integer totalClasses = new
		// CountTotalClassesMetric(ontology).getValue();
		// for (Set<OWLClass> sibling : siblings) {
		// countSiblings = countSiblings + sibling.size();
		// }
		// return /*1 -*/ ((float) countSiblings / (float) totalClasses);
		double sibs = (double) parser.getSibs().size();
		double noclassesImp = (double) parserI.getNoClasses(); //with imports

		if (noclassesImp > 0.0)
			return OntologyUtility.roundByGlobNK(sibs / noclassesImp);
		else
			return 0.0;
	}

	@Override
	public String toString() {
		return "Ratio of sibling fan-outness";
	}

}
