package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric;

import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.OntologyMetric;

public abstract class ClassAxoimsMetric extends OntologyMetric {
    public ClassAxoimsMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public String getLabel() {
	return "Class axioms";
    }
}
