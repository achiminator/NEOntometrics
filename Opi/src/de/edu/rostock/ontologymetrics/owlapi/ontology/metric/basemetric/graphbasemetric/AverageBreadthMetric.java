package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class AverageBreadthMetric extends GraphMetric {

	Logger myLogger = Logger.getLogger(this.getClass());

	public AverageBreadthMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Double getValue() {
		int n = 0;
		Iterator<TreeSet<OWLClass>> i = parserI.getLevels().iterator();
		while (i.hasNext())
			n += i.next().size();
		return OntologyUtility.roundByGlobNK((double) n / (double) parserI.getLevels().size());
	}

	@Override
	public String toString() {
		return "Average breadth";
	}

}
