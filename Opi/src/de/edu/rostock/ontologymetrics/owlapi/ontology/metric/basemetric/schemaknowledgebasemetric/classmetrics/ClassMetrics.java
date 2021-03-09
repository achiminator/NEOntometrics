package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.classmetrics;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.SchemaKnowledgeBaseMetric;

public abstract class ClassMetrics extends SchemaKnowledgeBaseMetric {

	private IRI classIRI; // IRI for the specific class

	public ClassMetrics(OWLOntology pOntology, IRI pIri) {
		super(pOntology);
		classIRI = pIri;
	}
	
	public ClassMetrics(OWLOntology pOntology, String pIri) {
		super(pOntology);
		classIRI = IRI.create(pIri);
	}

	@Override
	public String getLabel() {
		return "Class metrics";
	}

	public IRI getClassIRI() {
		return classIRI;
	}

	public void setClassIRI(IRI pIri) {
		classIRI = pIri;
	}

}
