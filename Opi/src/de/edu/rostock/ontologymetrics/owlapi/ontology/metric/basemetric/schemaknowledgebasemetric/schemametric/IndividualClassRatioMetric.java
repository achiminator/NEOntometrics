package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.schemametric;

import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountIndividualsMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountTotalClassesMetric;

public class IndividualClassRatioMetric extends SchemaMetric {

	public IndividualClassRatioMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	public Double getValue() {
		
		double individual = new CountIndividualsMetric(ontology).getValue();
		double classes = new CountTotalClassesMetric(ontology).getValue();
		if (classes == 0) {
			return 0.0;
		} else {
			return OntologyUtility.roundByGlobNK(individual / classes);
		}
	}

	public String toString() {
		return "Individual/class ratio";
	}
}
