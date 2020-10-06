package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

public class MaximalDepthMetric extends GraphMetric {

	
	
    public MaximalDepthMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
    	int n = 0;
		//Iterator<Set<OWLClass>> i = parserI.getLeaves().iterator();
    	Iterator<ArrayList<OWLClass>> i = parserI.getPathsAll().iterator(); //BL 08.09.2016 use allpaths
		int next = 0;
		while (i.hasNext()) {
			next = i.next().size()+1;
			if (n < next)
				n = next;
		}
		return n;
    }

    @Override
    public String toString() {
	return "Maximal depth";
    }

}
