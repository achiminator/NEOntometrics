package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.knowledgebasemetric;

import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.AbsoluteLeafCardinalityMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.AbsoluteRootCardinalityMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.AverageDepthMetric;

public class CohesionMetric extends KnowledgebaseMetric {

	public CohesionMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public String getValue() {
		double avg_depth = new AverageDepthMetric(ontology).getValue();
		int roots = new AbsoluteRootCardinalityMetric(ontology).getValue();
		int leaves = new AbsoluteLeafCardinalityMetric(ontology).getValue();

		return "NoR: " + roots + " - NoL: " + leaves + " - ADIT-LN: " + OntologyUtility.roundByGlobNK(avg_depth);
	}

	@Override
	public String toString() {
		return "Cohesion";
	}

}
