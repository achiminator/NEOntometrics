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

public class GraphMetric {
    private int absoluteRootCardinality;
    private int absoluteLeafCardinality;
    private int absoluteSibblingCardinality;
    private int absoluteDepth;
    private double averageDepth;
    private int maximalDepth;
    private int absoluteBreadth;
    private int maximalBreadth;
    private double averageBreadth;
    private double ratioOfLeafFanOutness;
    private double ratioOfSiblingFanOutness;
    private double tangledgness;
    private int totalNumberOfPaths;
    private double averageNumberOfPaths;

    public Map<String, Object> getAllMetrics(OWLOntology ontology, GraphParser parser, GraphParser parserI) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("Absoluterootcardinality", absoluteRootCardinality);
	returnObject.put("Absoluteleafcardinality", absoluteLeafCardinality);
	returnObject.put("Absolutesiblingcardinality", absoluteSibblingCardinality);
	returnObject.put("Absolutedepth", absoluteDepth);
	returnObject.put("Averagedepth", averageDepth);
	returnObject.put("Maximaldepth", maximalDepth);
	returnObject.put("Absolutebreadth", absoluteBreadth);
	returnObject.put("Averagebreadth", averageBreadth);
	returnObject.put("Maximalbreadth", maximalBreadth);
	returnObject.put("Ratioofleaffanoutness", ratioOfLeafFanOutness);
	returnObject.put("Ratioofsiblingfanoutness", ratioOfSiblingFanOutness);
	returnObject.put("Tangledness", tangledgness);
	returnObject.put("Totalnumberofpaths", totalNumberOfPaths);
	returnObject.put("Averagenumberofpaths", averageNumberOfPaths);
	return returnObject;
    }

    public Map<String, Object> calculateAllMetrics(OWLOntology ontology, GraphParser parser, GraphParser parserI) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("Absoluterootcardinality", absoluteRootCardinalityMetric(parser));
	returnObject.put("Absoluteleafcardinality", absoluteLeafCardinalityMetric(parser));
	returnObject.put("Absolutesiblingcardinality", absoluteSiblingCardinalityMetric(parser));
	returnObject.put("Absolutedepth", absoluteDepthMetric(parserI));
	returnObject.put("Averagedepth", averageDepthMetric(parserI));
	returnObject.put("Maximaldepth", maximalDepthMetric(parserI));
	returnObject.put("Absolutebreadth", absoluteBreadthMetric(parserI));
	returnObject.put("Averagebreadth", averageBreadthMetric(parserI));
	returnObject.put("Maximalbreadth", maximalBreadthMetric(parserI));
	returnObject.put("Ratioofleaffanoutness", ratioOfLeafFanOutnessMetric(parser, parserI));
	returnObject.put("Ratioofsiblingfanoutness", ratioOfSiblingFanOutnessMetric(parser, parserI));
	returnObject.put("Tangledness", tanglednessMetric(parserI));
	returnObject.put("Totalnumberofpaths", totalNumberOfPathsMetric(parserI));
	returnObject.put("Averagenumberofpaths", averageNumberOfPathsMetric(parserI));
	/*
	 * to implement
	 *
	 * graphMetrics.add(getDensityMetric()); graphMetrics.add(getLogicalAdaquacy());
	 * graphMetrics.add(getModularityMetric());
	 */
	return returnObject;
    }

    public int absoluteRootCardinalityMetric(GraphParser parser) {
	absoluteRootCardinality = parser.getRoots().size();
	return absoluteRootCardinality; // with imports
    }

    public int absoluteLeafCardinalityMetric(GraphParser parser) {
	absoluteLeafCardinality = parser.getLeaveClasses().size();
	return absoluteLeafCardinality;
    }

    public int absoluteSiblingCardinalityMetric(GraphParser parser) {
	absoluteSibblingCardinality = parser.getSibs().size();
	return absoluteSibblingCardinality;
    }

    public int absoluteDepthMetric(GraphParser parserI) {
	int n = 0;
	// Iterator<Set<OWLClass>> i = parserI.getLeaves().iterator();
	Iterator<ArrayList<OWLClass>> i = parserI.getPathsAll().iterator(); // BL 08.09.2016 all paths to all nodes ->
									    // absolute depth
	while (i.hasNext()) {
	    n += i.next().size() + 1;
	}
	absoluteDepth = n;
	return n;
	// return parserI.getPathsAll().size();
    }

    public double averageDepthMetric(GraphParser parserI) {
	double n = 0;
	// Iterator<Set<OWLClass>> i = parserI.getLeaves().iterator();
	Iterator<ArrayList<OWLClass>> i = parserI.getPathsAll().iterator(); // BL 08.09.2016 use allpaths
	while (i.hasNext())
	    n += i.next().size() + 1;
	averageDepth = OntologyUtility.roundByGlobNK((double) n / (double) parserI.getPathsAll().size());
	return averageDepth;

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
	maximalDepth = n;
	return n;
    }

    public int absoluteBreadthMetric(GraphParser parserI) {
	int n = 0;
	Iterator<TreeSet<OWLClass>> i = parserI.getLevels().iterator();
	while (i.hasNext()) {
	    n += i.next().size();
	}
	absoluteBreadth = n;
	return n;
    }

    public double averageBreadthMetric(GraphParser parserI) {
	int n = 0;
	Iterator<TreeSet<OWLClass>> i = parserI.getLevels().iterator();
	while (i.hasNext())
	    n += i.next().size();
	averageBreadth = OntologyUtility.roundByGlobNK((double) n / (double) parserI.getLevels().size());
	return averageBreadth;

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
	maximalBreadth = n;
	return n;
    }

    public double ratioOfLeafFanOutnessMetric(GraphParser parser, GraphParser parserI) {
	double leaves = (double) parser.getLeaveClasses().size();
	double noclassesImp = (double) parserI.getNoClasses(); // with imports

	if (noclassesImp > 0.0)
	    ratioOfLeafFanOutness = OntologyUtility.roundByGlobNK(leaves / noclassesImp);
	else
	    ratioOfLeafFanOutness = 0.0;
	return ratioOfLeafFanOutness;

    }

    public double ratioOfSiblingFanOutnessMetric(GraphParser parser, GraphParser parserI) {
	double sibs = (double) parser.getSibs().size();
	double noclassesImp = (double) parserI.getNoClasses(); // with imports

	if (noclassesImp > 0.0)
	    ratioOfSiblingFanOutness = OntologyUtility.roundByGlobNK(sibs / noclassesImp);
	else
	    ratioOfSiblingFanOutness = 0.0;
	return ratioOfSiblingFanOutness;
    }

    public double tanglednessMetric(GraphParser parserI) {
	tangledgness =  OntologyUtility.roundByGlobNK((double) parserI.getTangledClasses().size() / (double) parserI.getNoClasses());
	return tangledgness;  

    }

    public int totalNumberOfPathsMetric(GraphParser parserI) {
	totalNumberOfPaths = parserI.getPathsAll().size();
	return totalNumberOfPaths; // with imports
    }

    public double averageNumberOfPathsMetric(GraphParser parserI) {
	int gen = parserI.getGenerations().size();
	int pathsAll = parserI.getPathsAll().size();

	if (gen > 0)
	    averageNumberOfPaths = OntologyUtility.roundByGlobNK((double) pathsAll / (double) gen);
	else
	    averageNumberOfPaths = 0.0;
	return averageNumberOfPaths;
    }
}
