package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.classmetrics;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountClassChildrenMetric extends ClassMetrics {

    public CountClassChildrenMetric(OWLOntology pOntology, IRI pIri) {
	super(pOntology, pIri);
    }

    @Override
    public Integer getValue() {
	int subClasses = 0;

	OWLClass cls = OntologyUtility.getClass(ontology, getClassIRI());

	if (cls != null) {
		//MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    subClasses = EntitySearcher.getSubClasses(cls,ontology).size();
	    if (subClasses > 0) {
		for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls,ontology)) {
		    subClasses = subClasses
			    + new CountClassChildrenMetric(ontology, subClass
				    .asOWLClass().getIRI()).getValue();
		}
	    }
	}

	return subClasses;
    }

    @Override
    public String toString() {
	return "Class children count";
    }

}
