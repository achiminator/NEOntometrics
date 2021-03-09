package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

public class AbsoluteDepthMetric extends GraphMetric {
    public AbsoluteDepthMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Integer getValue() {
    	int n = 0;
		//Iterator<Set<OWLClass>> i = parserI.getLeaves().iterator();
    	Iterator<ArrayList<OWLClass>> i = parserI.getPathsAll().iterator(); //BL 08.09.2016 all paths to all nodes -> absolute depth
		while (i.hasNext()){
			n+= i.next().size()+1;
		}
		
		return n;
		//return parserI.getPathsAll().size();
    }

    @Override
    public String toString() {
	return "Absolute depth";
    }

}
