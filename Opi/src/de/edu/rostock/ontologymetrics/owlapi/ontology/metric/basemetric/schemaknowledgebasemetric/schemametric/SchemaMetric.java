package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.schemametric;

import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.SchemaKnowledgeBaseMetric;

public abstract class SchemaMetric extends SchemaKnowledgeBaseMetric {

    public SchemaMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    public String getLabel() {
	return "Schema metrics";
    }

}
