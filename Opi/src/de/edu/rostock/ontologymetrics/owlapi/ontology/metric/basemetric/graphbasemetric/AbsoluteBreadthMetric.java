package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

public class AbsoluteBreadthMetric extends GraphMetric {

	Logger myLogger = Logger.getLogger(this.getClass());

	public AbsoluteBreadthMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Integer getValue() {
		int n = 0;
		Iterator<TreeSet<OWLClass>> i = parserI.getLevels().iterator();
		while (i.hasNext()) {
			n += i.next().size();
		}
		return n;
	}

	@Override
	public String toString() {
		return "Absolute breadth";
	}

}
