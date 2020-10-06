package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class AverageDepthMetric extends GraphMetric {

	
	
    public AverageDepthMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Double getValue() {
    	double n = 0;
		//Iterator<Set<OWLClass>> i = parserI.getLeaves().iterator();
    	Iterator<ArrayList<OWLClass>> i = parserI.getPathsAll().iterator(); //BL 08.09.2016 use allpaths
       	while (i.hasNext())
			n += i.next().size()+1;
		return OntologyUtility.roundByGlobNK((double)n / (double)parserI.getPathsAll().size());
    }

    @Override
    public String toString() {
	return "Average depth";
    }

}
