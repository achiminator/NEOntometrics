package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.classmetrics;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher; //MLIC: Umstellung OWL API von 3.5 auf 4.2.3

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

/**
 * Return the sum of the number attributes that are comments and the number of
 * attributes that are labels of the given class.
 */
public class ClassReadabilityMetric extends ClassMetrics {

	public ClassReadabilityMetric(OWLOntology pOntology, IRI pIri) {
		super(pOntology, pIri);
	}

	@Override
	public Integer getValue() {
		OWLClass cls = OntologyUtility.getClass(ontology, getClassIRI());
		int countAnnotations = 0;

		if (cls != null) {
			// MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
			for (OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ontology)) {
				if (annotation.getProperty().isLabel() || annotation.getProperty().isComment()) {
					countAnnotations++;
				}
			}
		}
		return countAnnotations;
	}

	@Override
	public String toString() {
		return "Class readability";
	}

}
