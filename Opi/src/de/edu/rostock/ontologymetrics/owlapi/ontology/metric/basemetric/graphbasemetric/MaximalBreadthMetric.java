package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

public class MaximalBreadthMetric extends GraphMetric {

	Logger myLogger = Logger.getLogger(this.getClass());

	public MaximalBreadthMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		int n = 0;
		Iterator<TreeSet<OWLClass>> i = parserI.getLevels().iterator();
		int next = 0;
		while (i.hasNext()) {
			next = i.next().size();
			if (n < next)
				n = next;
		}
		return n;
	}

	@Override
	public String toString() {
		return "Maximal breadth";
	}

}
