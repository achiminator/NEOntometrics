package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.schemametric;

import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountAxiomsMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountTotalClassesMetric;

public class AxiomClassRatioMetric extends SchemaMetric {

	public AxiomClassRatioMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	public Double getValue() {
		
		double axioms = new CountAxiomsMetric(ontology).getValue();
		double classes = new CountTotalClassesMetric(ontology).getValue();
		if (classes == 0) {
			return 0.0;
		} else {
			return OntologyUtility.roundByGlobNK(axioms / classes);
		}
	}

	public String toString() {
		return "Axiom/class ratio";
	}
}
