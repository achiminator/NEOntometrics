package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.security.IdentityScope;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import javax.print.attribute.standard.NumberUp;

import org.obolibrary.oboformat.parser.XrefExpander.IsaExpansion;
import org.semanticweb.owlapi.OWLAPIParsersModule;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.GraphParser;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class GraphMetric implements Callable<GraphMetric> {
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
    private int sumOfclassesWithMoreThanOneDirectAncestor;
    private int numberOfPathLenth;
    private double tangledgness;
    private int totalNumberOfPaths;
    private double averageNumberOfPaths;

    private GraphParser parser;
    private GraphParser parserI;
    private boolean withImports;
    private OWLOntology ontology;
    private int sumOfPaths;
    private int sumOfDirectAncestorClasses;
    private int sumOfDirectAncestorOfLeafClasses;
    private Object sumOfDirectAncestorWithMoreThanOneDirectAncestor;

    public Map<String, Object> getAllMetrics() {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("Absoluterootcardinality", absoluteRootCardinality);
	returnObject.put("Absoluteleafcardinality", absoluteLeafCardinality);
	returnObject.put("Absolutesiblingcardinality", absoluteSibblingCardinality);
	returnObject.put("Absolutedepth", absoluteDepth);
	returnObject.put("Averagedepth", averageDepth);
	returnObject.put("Maximaldepth", maximalDepth);
	returnObject.put("Absolutebreadth", absoluteBreadth);
	returnObject.put("Averagebreadth", averageBreadth);
	returnObject.put("ClassesWithMoreThanOneAncestor", sumOfclassesWithMoreThanOneDirectAncestor);
	returnObject.put("SumOfDirectAncestorClasses", sumOfDirectAncestorClasses);
	returnObject.put("SumOfDirectAncestorClasses", sumOfDirectAncestorClasses);
	returnObject.put("SumOfDirectAncestorOfLeafClasses", sumOfDirectAncestorOfLeafClasses);
	returnObject.put("sumOfDirectAncestorWithMoreThanOneDirectAncestor",
		sumOfDirectAncestorWithMoreThanOneDirectAncestor);

	returnObject.put("Numberofpaths", numberOfPathLenth);
	returnObject.put("Sumofpaths", sumOfPaths);
	returnObject.put("Maximalbreadth", maximalBreadth);
	returnObject.put("Ratioofleaffanoutness", ratioOfLeafFanOutness);
	returnObject.put("Ratioofsiblingfanoutness", ratioOfSiblingFanOutness);
	returnObject.put("Tangledness", tangledgness);
	returnObject.put("Totalnumberofpaths", totalNumberOfPaths);
	returnObject.put("Averagenumberofpaths", averageNumberOfPaths);

	return returnObject;
    }

    public GraphMetric call() {

	iterativeMetrics(true);
	absoluteSiblingCardinalityMetric(withImports);
	absoluteDepthMetric(withImports);
	averageDepthMetric(withImports);
	maximalDepthMetric(withImports);
	absoluteBreadthMetric(withImports);
	// calculateNumberOfPaths(withImports);
	averageBreadthMetric(withImports);
	maximalBreadthMetric(withImports);

//	ratioOfLeafFanOutnessMetric(parser, withImports);
//	ratioOfSiblingFanOutnessMetric(parser, withImports);
	tanglednessMetric(withImports);
	totalNumberOfPathsMetric(withImports);
	averageNumberOfPathsMetric(withImports);
	/*
	 * to implement
	 *
	 * graphMetrics.add(getDensityMetric()); graphMetrics.add(getLogicalAdaquacy());
	 * graphMetrics.add(getModularityMetric());
	 */
	return this;
    }

    public GraphMetric(OWLOntology ontology, GraphParser parser, GraphParser parserI, boolean withImports) {
	this.parser = parser;
	this.parserI = parserI;
	this.withImports = withImports;
	this.ontology = ontology;
    }

    /**
     * Calculates such Metrics that need an iteration over the classes of the
     * Metrics. They are all from different categories, though the boundling should
     * save computational resources. This includes: leafcardinalty, rootcardinality,
     * pathlengths, Annotation/Class
     * 
     * @param withImports
     */
    public void iterativeMetrics(boolean withImports) {
	Set<OWLClass> ontologyClasses; // All unique Ontology Classes of the given ontology
	Set<OWLClass> leafClasses = new TreeSet<OWLClass>(); // Container for all leaf Classes of this ontology (Such
							     // without other subclasses)
	Set<OWLClass> upperClasses = new TreeSet<OWLClass>(); // Container for the opposite, for classes (such that have
							      // leaves)
	ontologyClasses = ontology.getClassesInSignature(OntologyUtility.ImportClosures(withImports));
	Iterator<OWLClass> i = ontologyClasses.iterator();
	{
	    OWLClass next;
	    // Identify the upper and leaf classes in this ontology
	    while (i.hasNext()) {
		next = i.next();
		if (next.isBuiltIn())
		    continue;
		Collection<OWLClassExpression> subClassExpr = EntitySearcher.getSubClasses(next, ontology);
		boolean hasSubClass = false;
		for (OWLClassExpression owlClassExpression : subClassExpr) {
		    // For this calculation, consider only Real Classes in the subclass tree, not
		    // statements like "isEncded exactly 1 owl:thing"
		    if (owlClassExpression.isClassExpressionLiteral()) {
			hasSubClass = true;
			upperClasses.add(next);
		    }
		}
		if (!hasSubClass)
		    leafClasses.add(next);
	    }
	}
	Set<OWLClass> classesWithMoreThanOneDirectAncestor = getClassesWithAncestors(ontologyClasses, 2);
	sumOfclassesWithMoreThanOneDirectAncestor = classesWithMoreThanOneDirectAncestor.size();
	sumOfDirectAncestorOfLeafClasses = getAncestorClasses(leafClasses, 1).size();
	sumOfDirectAncestorClasses = getAncestorClasses(ontologyClasses, 1).size();
	sumOfDirectAncestorWithMoreThanOneDirectAncestor = getAncestorClasses(classesWithMoreThanOneDirectAncestor, 1)
		.size();

	absoluteLeafCardinality = leafClasses.size();
	absoluteRootCardinality = upperClasses.size();

	// Now calculate the number of Paths from the leaf classes to OWL-Thing
	numberOfPathLenth = 0;
	for (OWLClass owlClass : leafClasses) {
	    findPathsToTop(owlClass, 0);
	}
    }

    /**
     * Recursive method for calculatingthe number of Paths from the bottom of an
     * ontology to the top (owl:Thing)
     * 
     * @param owlClass The class where the search starts upwards
     * @param counter  of paths
     */
    protected void findPathsToTop(OWLClass owlClass, int counter) {
	
	Collection<OWLClassExpression> superClassesExprOfLeaf = EntitySearcher.getSuperClasses(owlClass, ontology);
	for (OWLClassExpression superClassExprOfLeaf : superClassesExprOfLeaf) {
	    if (!superClassExprOfLeaf.isClassExpressionLiteral())
		continue;
	    for (OWLClass superClassOfLeaf : superClassExprOfLeaf.getClassesInSignature()) {
		sumOfPaths++;
		if (superClassOfLeaf.isOWLThing() && superClassExprOfLeaf.getClassesInSignature().size() == 1)
		    numberOfPathLenth += 1;
		else
		    findPathsToTop(superClassOfLeaf, counter);
	    }

	}
    }

    public int absoluteSiblingCardinalityMetric(boolean withImports) {
	if (withImports)
	    absoluteSibblingCardinality = parserI.getSibs().size();
	else
	    absoluteSibblingCardinality = parser.getSibs().size();
	return absoluteSibblingCardinality;
    }
/**
 * Gets the AncestorClasses of a given set of OWLClasses
 * @param ontologyClasses Set of ontology classes that are searched
 * @param minAmountOfAncestors A class must have at least ... ancestors to be included
 * @return Found Ancestors
 */
    protected List<OWLClass> getAncestorClasses(Set<OWLClass> ontologyClasses, int minAmountOfAncestors) {
	Iterator<OWLClass> i = ontologyClasses.iterator();

	List<OWLClass> ancestorClasses = new LinkedList<OWLClass>();
	OWLClass next;
	while (i.hasNext()) {
	    next = i.next();
	    Set<OWLClass> superClasses = new TreeSet<OWLClass>();
	    for (OWLClassExpression owlExpression : EntitySearcher.getSuperClasses(next, ontology)) {
		if (owlExpression instanceof OWLClassImpl)
		    superClasses.add(owlExpression.asOWLClass());

	    }
	    if (superClasses.size() >= minAmountOfAncestors)
		ancestorClasses.addAll(superClasses);

	}
	return ancestorClasses;
    }

    /**
     * Gets the classes that have ancestors
     * 
     * @param ontologyClasses      Set of OntologyClasses that are searched.
     * @param minAmountOfAncestors Filters the minimum of ancestors to be included.
     *                             E.g., 2, then a class must have at least two
     *                             ancestors to be included
     * @return Set of Classes that have at least minAmountOfAncestors
     */
    protected Set<OWLClass> getClassesWithAncestors(Set<OWLClass> ontologyClasses, int minAmountOfAncestors) {
	Iterator<OWLClass> i = ontologyClasses.iterator();
	Set<OWLClass> results = new TreeSet<OWLClass>();
	OWLClass next;
	while (i.hasNext()) {
	    next = i.next();
	    Set<OWLClass> superClasses = new TreeSet<OWLClass>();
	    for (OWLClassExpression owlExpression : EntitySearcher.getSuperClasses(next, ontology)) {
		if (owlExpression instanceof OWLClassImpl)
		    superClasses.add(owlExpression.asOWLClass());

	    }
	    if (superClasses.size() >= minAmountOfAncestors)
		results.add(next);

	}
	return results;
    }

    public int calculateNumberOfPaths(boolean withImports) {
	OWLDataFactory df = new OWLDataFactoryImpl();
	numberOfPathLenth = numberofPaths(df.getOWLThing(), absoluteBreadth);
absoluteDepth = 234;
	return numberOfPathLenth;
    }

    private int numberofPaths(OWLClass owlClass, int foundGraphEnds) {
	//absoluteDepth++;
	Collection<OWLClassExpression> subClassExpressions = EntitySearcher.getSubClasses(owlClass, ontology);
	Set<OWLClass> subclasses = new TreeSet<OWLClass>();
	for (OWLClassExpression owlClassExpression : subClassExpressions) {
	    subclasses.addAll(owlClassExpression.getClassesInSignature());
	}

	if (subclasses.size() == 0)
	    foundGraphEnds++;
	else {
	    for (OWLClass subclass : subclasses) {
		this.numberofPaths(subclass, foundGraphEnds);
	    }
	}

	return foundGraphEnds;

    }

    public int absoluteDepthMetric(boolean withImports) {

	int n = 0;
	// Iterator<Set<OWLClass>> i = parserI.getLeaves().iterator();
	Iterator<ArrayList<OWLClass>> i;
	if (withImports)
	    i = parserI.getPathsAll().iterator(); // BL 08.09.2016 all paths to all nodes ->
	else
	    i = parser.getPathsAll().iterator(); // BL 08.09.2016 all paths to all nodes ->
						 // absolute depth

	while (i.hasNext()) {
	    n += i.next().size() + 1;
	}
	absoluteDepth = n;
	return n;
	// return parserI.getPathsAll().size();
    }

    public double averageDepthMetric(boolean withImports) {
	double n = 0;
	// Iterator<Set<OWLClass>> i = parserI.getLeaves().iterator();
	Iterator<ArrayList<OWLClass>> i;
	if (withImports)
	    i = parserI.getPathsAll().iterator(); // BL 08.09.2016 use allpaths
	else
	    i = parser.getPathsAll().iterator(); // BL 08.09.2016 use allpaths

	while (i.hasNext())
	    n += i.next().size() + 1;
	averageDepth = OntologyUtility.roundByGlobNK((double) n / (double) parserI.getPathsAll().size());
	return averageDepth;

    }

    public int maximalDepthMetric(boolean withImports) {
	int n = 0;
	// Iterator<Set<OWLClass>> i = parserI.getLeaves().iterator();
	Iterator<ArrayList<OWLClass>> i;
	if (withImports)
	    i = parserI.getPathsAll().iterator(); // BL 08.09.2016 use allpaths
	else
	    i = parser.getPathsAll().iterator(); // BL 08.09.2016 use allpaths
	int next = 0;
	while (i.hasNext()) {
	    next = i.next().size() + 1;
	    if (n < next)
		n = next;
	}
	maximalDepth = n;
	return n;
    }

    public int absoluteBreadthMetric(boolean withImports) {
	int n = 0;
	Iterator<TreeSet<OWLClass>> i;
	if (withImports)
	    i = parserI.getLevels().iterator();
	else
	    i = parser.getLevels().iterator();
	while (i.hasNext()) {
	    n += i.next().size();
	}
	absoluteBreadth = n;
	return n;
    }

    public double averageBreadthMetric(boolean withImports) {
	int n = 0;
	Iterator<TreeSet<OWLClass>> i = parserI.getLevels().iterator();
	while (i.hasNext())
	    n += i.next().size();
	averageBreadth = OntologyUtility.roundByGlobNK((double) n / (double) parserI.getLevels().size());
	return averageBreadth;

    }

    public int maximalBreadthMetric(boolean withImports) {
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


    public double tanglednessMetric(boolean withImports) {
	if (withImports)
	    tangledgness = OntologyUtility
		    .roundByGlobNK((double) parserI.getTangledClasses().size() / (double) parserI.getNoClasses());
	else
	    tangledgness = OntologyUtility
		    .roundByGlobNK((double) parser.getTangledClasses().size() / (double) parser.getNoClasses());

	return tangledgness;

    }

    public int totalNumberOfPathsMetric(boolean withImports) {
	totalNumberOfPaths = parserI.getPathsAll().size();
	return totalNumberOfPaths; // with imports
    }

    public double averageNumberOfPathsMetric(boolean withImports) {
	int gen;
	int pathsAll;
	if (withImports) {
	    gen = parserI.getGenerations().size();
	    pathsAll = parserI.getPathsAll().size();
	} else {
	    gen = parser.getGenerations().size();
	    pathsAll = parser.getPathsAll().size();
	}
	if (gen > 0)
	    averageNumberOfPaths = OntologyUtility.roundByGlobNK((double) pathsAll / (double) gen);
	else
	    averageNumberOfPaths = 0.0;
	return averageNumberOfPaths;
    }
}
