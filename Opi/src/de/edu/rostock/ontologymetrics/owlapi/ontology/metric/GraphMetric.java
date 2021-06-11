package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.GraphParser;

class GraphMetric {
    public Map<String, Object> getAllMetrics(OWLOntology ontology, GraphParser parser, GraphParser parserI) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("Absolute root cardinality", absoluteRootCardinalityMetric(parser));
	returnObject.put("Absolute leaf cardinality", absoluteLeafCardinalityMetric(parser));
	returnObject.put("Absolute sibling cardinality", absoluteSiblingCardinalityMetric(parser));
	returnObject.put("Absolute depth", absoluteDepthMetric(parserI));
	returnObject.put("Average depth", averageDepthMetric(parserI));
	returnObject.put("Maximal depth", maximalDepthMetric(parserI));
	returnObject.put("Absolute breadth", absoluteBreadthMetric(parserI));
	returnObject.put("Average breadth", averageBreadthMetric(parserI));
	returnObject.put("Maximal breadth", maximalBreadthMetric(parserI));
	returnObject.put("Ratio of leaf fan-outness", ratioOfLeafFanOutnessMetric(parser, parserI));
	returnObject.put("Ratio of sibling fan-outness", ratioOfSiblingFanOutnessMetric(parser, parserI));
	returnObject.put("Tangledness", tanglednessMetric(parserI));
	returnObject.put("Total number of paths", totalNumberOfPathsMetric(parserI));
	returnObject.put("Averagenumber of paths", averageNumberOfPathsMetric(parserI));
	/*
	 * to implement
	 *
	 * graphMetrics.add(getDensityMetric()); graphMetrics.add(getLogicalAdaquacy());
	 * graphMetrics.add(getModularityMetric());
	 */
	return returnObject;
    }

    public int absoluteRootCardinalityMetric(GraphParser parser) {
	return parser.getRoots().size(); // with imports
    }

    public int absoluteLeafCardinalityMetric(GraphParser parser) {
	return parser.getLeaveClasses().size();
    }

    public int absoluteSiblingCardinalityMetric(GraphParser parser) {
	return parser.getSibs().size();
    }

    public int absoluteDepthMetric(GraphParser parserI) {
	int n = 0;
	// Iterator<Set<OWLClass>> i = parserI.getLeaves().iterator();
	Iterator<ArrayList<OWLClass>> i = parserI.getPathsAll().iterator(); // BL 08.09.2016 all paths to all nodes ->
									    // absolute depth
	while (i.hasNext()) {
	    n += i.next().size() + 1;
	}

	return n;
	// return parserI.getPathsAll().size();
    }

    public double averageDepthMetric(GraphParser parserI) {
	double n = 0;
	// Iterator<Set<OWLClass>> i = parserI.getLeaves().iterator();
	Iterator<ArrayList<OWLClass>> i = parserI.getPathsAll().iterator(); // BL 08.09.2016 use allpaths
	while (i.hasNext())
	    n += i.next().size() + 1;
	return OntologyUtility.roundByGlobNK((double) n / (double) parserI.getPathsAll().size());

    }

    public int maximalDepthMetric(GraphParser parserI) {
	int n = 0;
	// Iterator<Set<OWLClass>> i = parserI.getLeaves().iterator();
	Iterator<ArrayList<OWLClass>> i = parserI.getPathsAll().iterator(); // BL 08.09.2016 use allpaths
	int next = 0;
	while (i.hasNext()) {
	    next = i.next().size() + 1;
	    if (n < next)
		n = next;
	}
	return n;
    }

    public int absoluteBreadthMetric(GraphParser parserI) {
	int n = 0;
	Iterator<TreeSet<OWLClass>> i = parserI.getLevels().iterator();
	while (i.hasNext()) {
	    n += i.next().size();
	}
	return n;
    }

    public double averageBreadthMetric(GraphParser parserI) {
	int n = 0;
	Iterator<TreeSet<OWLClass>> i = parserI.getLevels().iterator();
	while (i.hasNext())
	    n += i.next().size();
	return OntologyUtility.roundByGlobNK((double) n / (double) parserI.getLevels().size());

    }

    public int maximalBreadthMetric(GraphParser parserI) {
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

    public double ratioOfLeafFanOutnessMetric(GraphParser parser, GraphParser parserI) {
	double leaves = (double) parser.getLeaveClasses().size();
	double noclassesImp = (double) parserI.getNoClasses(); // with imports

	if (noclassesImp > 0.0)
	    return OntologyUtility.roundByGlobNK(leaves / noclassesImp);
	else
	    return 0.0;
    }

    public double ratioOfSiblingFanOutnessMetric(GraphParser parser, GraphParser parserI) {
	double sibs = (double) parser.getSibs().size();
	double noclassesImp = (double) parserI.getNoClasses(); // with imports

	if (noclassesImp > 0.0)
	    return OntologyUtility.roundByGlobNK(sibs / noclassesImp);
	else
	    return 0.0;
    }

    public double tanglednessMetric(GraphParser parserI) {
	return OntologyUtility
		.roundByGlobNK((double) parserI.getTangledClasses().size() / (double) parserI.getNoClasses()); // with
													       // imports
    }
    public int totalNumberOfPathsMetric(GraphParser parserI) {
	return parserI.getPathsAll().size(); //with imports
    }
    public double averageNumberOfPathsMetric(GraphParser parserI) {
	int gen = parserI.getGenerations().size();
 	int pathsAll = parserI.getPathsAll().size();
 
	if (gen > 0)
		return OntologyUtility.roundByGlobNK((double)pathsAll / (double)gen);
	else
		return 0.0;
    }
}
