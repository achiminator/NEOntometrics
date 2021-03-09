package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.BaseMetric;

public abstract class SchemaKnowledgeBaseMetric extends BaseMetric {

    public SchemaKnowledgeBaseMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    protected int countTotalInstancesOf(OWLClass cls) {
	int count = 0;
	if (cls != null) {
		//MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    count = EntitySearcher.getIndividuals(cls,ontology).size();
	    for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls,ontology)) {
		count = count + countTotalInstancesOf(subClass.asOWLClass());
	    }
	}
	return count;
    }

}
