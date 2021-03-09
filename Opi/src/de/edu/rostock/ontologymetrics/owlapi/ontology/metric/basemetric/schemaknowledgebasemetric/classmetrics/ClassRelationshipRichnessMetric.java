package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.classmetrics;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class ClassRelationshipRichnessMetric extends ClassMetrics {

	public ClassRelationshipRichnessMetric(OWLOntology pOntology, IRI pIri) {
		super(pOntology, pIri);
	}

	@Override
	public Integer getValue() {
		int noRelations = 0;
		OWLClass next = OntologyUtility.getClass(ontology, getClassIRI());
		for (OWLObjectProperty objProperty : next.getObjectPropertiesInSignature()) {
			if (EntitySearcher.isFunctional(objProperty, ontology/*.getImports()*/)) {
				noRelations += next.getObjectPropertiesInSignature().size();
			}
		}
		return noRelations;
	}

	@Override
	public String toString() {
		return "Class relationship richness";
	}

}
