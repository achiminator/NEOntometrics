package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.knowledgebasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.SchemaKnowledgeBaseMetric;

public abstract class KnowledgebaseMetric extends SchemaKnowledgeBaseMetric {

    public KnowledgebaseMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public String getLabel() {
	return "Knowledgebase metrics";
    }

}
