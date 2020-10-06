package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

public class CountNumberOfClassesMetric extends BaseMetric {

	public CountNumberOfClassesMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		return (Integer) 0;
	}

	// new mlic
	public Integer getValue(OWLClass cls) {
		int countClasses = 0;

		if (cls != null) {
			countClasses++;
			// MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
			if (!EntitySearcher.getSubClasses(cls, ontology).isEmpty()) {
				for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls, ontology.getImportsClosure())) {
					countClasses = countClasses + getValue(subClass.asOWLClass());
				}
			}
		}

		return countClasses;
	}

	@Override
	public String toString() {
		return "Number of classes";
	}

}
