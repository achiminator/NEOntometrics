package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.classmetrics;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class CountClassInstancesMetric extends ClassMetrics {

    public CountClassInstancesMetric(OWLOntology pOntology, IRI pIri) {
	super(pOntology, pIri);
    }

    @Override
    public Integer getValue() {
	int instances = 0;

	OWLClass cls = OntologyUtility.getClass(ontology, getClassIRI());

	if (cls != null) {
		//MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    instances = EntitySearcher.getIndividuals(cls,ontology).size();
	    
	    //MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    if (!EntitySearcher.getSubClasses(cls,ontology).isEmpty()) {
	    	
	    //MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls,ontology)) {
		    instances = instances
			    + new CountClassInstancesMetric(ontology, subClass
				    .asOWLClass().getIRI()).getValue();
		}
	    }
	}

	return instances;
    }

    @Override
    public String toString() {
	return "Class instances count";
    }

}
