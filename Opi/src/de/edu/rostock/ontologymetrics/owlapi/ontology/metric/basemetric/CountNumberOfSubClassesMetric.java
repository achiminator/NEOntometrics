package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

public class CountNumberOfSubClassesMetric extends BaseMetric {

	public CountNumberOfSubClassesMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		return (Integer) 0;
	}

	// new mlic
	public Integer getValue(OWLClass cls) {
		int subClasses = 0;

		if (cls != null) {
			// MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
			subClasses = EntitySearcher.getSubClasses(cls, ontology).size();
			if (subClasses > 0) {
				for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls, ontology)) {
					subClasses = subClasses + getValue(subClass.asOWLClass());
				}
			}
		}
		return subClasses;
	}

	@Override
	public String toString() {
		return "Number of subclasses";
	}

}
