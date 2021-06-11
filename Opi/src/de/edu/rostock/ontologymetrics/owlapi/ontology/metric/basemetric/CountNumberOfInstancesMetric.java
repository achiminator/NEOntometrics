package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.BaseMetric;

public class CountNumberOfInstancesMetric extends BaseMetric {

	public CountNumberOfInstancesMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		return null;
	}

	// new mlic
	public Integer getValue(OWLClass cls) {
		int instances = 0;

		if (cls != null) {
			// MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
			instances = EntitySearcher.getIndividuals(cls, ontology).size();

			// MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
			if (!EntitySearcher.getSubClasses(cls, ontology).isEmpty()) {
				for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls, ontology)) {
					instances = instances + getValue(subClass.asOWLClass());
				}
			}
		}

		return instances;
	}

	@Override
	public String toString() {
		return "Number of instances";
	}

}
