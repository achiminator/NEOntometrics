package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.classmetrics;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountTotalClassesMetric;

/**
 * Return the average number of subclasses per class in the subtree rooted at
 * the given class of IRI.
 */
public class ClassInheritenceRichnessMetric extends ClassMetrics {

    public ClassInheritenceRichnessMetric(OWLOntology pOntology, IRI pIri) {
	super(pOntology, pIri);
    }

    @Override
    public Double getValue() {
    // the average number of subclasses per class
	int countClasses = new CountTotalClassesMetric/*CountClassesMetric*/(ontology).getValue();
	int countSubClasses = new CountClassChildrenMetric(ontology, getClassIRI()).getValue();

	// avoid a division by zero
	if (countSubClasses == 0) {
	    return 0.0;
	} else {
	    return OntologyUtility.roundByGlobNK((double) countClasses / (double) countSubClasses);
	}
    }

    @Override
    public String toString() {
	return "Class inheritance richness";
    }

}
