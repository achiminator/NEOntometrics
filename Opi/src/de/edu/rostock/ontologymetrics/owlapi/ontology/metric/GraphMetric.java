package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.GraphParser;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

public class GraphMetric extends MetricCalculations implements Callable<GraphMetric> {

    private GraphParser parser;
    private GraphParser parserI;
    private boolean withImports;

    private int sumOfPathToLeafClasses = 0;
    private int totalDepth = 0;
    private int maxDepth = 0;
    private int minDepth = 999990;
    private LinkedHashMap<Integer, Set<OWLClass>> owlClassToDepth; // Links the classes in the ontology to a hierachy
								   // level.

    public GraphMetric call() {

	absoluteSiblingCardinalityMetric();

	tanglednessMetric();
	calculateNumberOfPaths();
	calculateBreathMetrics();
	return this;
    }

    public GraphMetric(OWLOntology ontology, GraphParser parser, GraphParser parserI, boolean withImports) {
	super(ontology, withImports);
	this.parser = parser;
	this.parserI = parserI;

    }

    private void absoluteSiblingCardinalityMetric() {
	int absoluteSibblingCardinality;
	if (withImports)
	    absoluteSibblingCardinality = parserI.getSibs().size();
	else
	    absoluteSibblingCardinality = parser.getSibs().size();
	returnObject.put("Absolutesiblingcardinality", absoluteSibblingCardinality);

    }

    /**
     * Gets the AncestorClasses of a given set of OWLClasses
     * 
     * @param ontologyClasses      Set of ontology classes that are searched
     * @param minAmountOfAncestors A class must have at least ... ancestors to be
     *                             included
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
    private Set<OWLClass> getClassesWithAncestors(Set<OWLClass> ontologyClasses, int minAmountOfAncestors) {
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

    /**
     * Calculates such Metrics that need an iteration over the classes of the
     * Metrics. They are all from different categories, though the boundling should
     * save computational resources. This includes: leafcardinalty, rootcardinality,
     * pathlengths, Annotation/Class
     * 
     * @param withImports
     */
    private void calculateNumberOfPaths() {

	// At first, get the root class. A root class is a class that does not have any
	// ancestors/Superclasses. At the same time, we can also identify the leaf and
	// rootclasses.
	Set<OWLClass> leafClasses = new TreeSet<OWLClass>(); // Container for all leaf Classes of this ontology (Such
							     // without other subclasses)
	Set<OWLClass> upperClasses = new TreeSet<OWLClass>(); // Container for the opposite, for classes (such that have
							      // leaves)
	Set<OWLClass> rootClasses = new TreeSet<OWLClass>();
	Set<OWLClass> owlClasses = ontology.getClassesInSignature(OntologyUtility.ImportClosures(withImports));
	
	// Iterate over all classes
	for (OWLClass owlClass : owlClasses) {

	    // If a thing does not have superclasses, then it is a root class
	    Collection<OWLClassExpression> superClassesofOwlClass = EntitySearcher.getSuperClasses(owlClass, ontology);
	    if (superClassesofOwlClass.size() < 1)
		rootClasses.add(owlClass);
	    Collection<OWLClassExpression> subClassExpr = EntitySearcher.getSubClasses(owlClass, ontology);
	    Collection<OWLClass> subClasses = OntologyUtility.classExpr2classes(subClassExpr);
	    if (subClasses.size() < 1)
		// If a classes does not have any subclasses, it is a leaf class. As owl-Thing
		// is always on top, we do not count it as a root class.
		leafClasses.add(owlClass);
	    else {
		// if a class has at least one subclass, it is a upper class
		if (!owlClass.isBuiltIn())
		    // We are only interested in the classes that are part of the reasoned or
		    // asserted knowledge. Built-in classes like owl:Thing are disregarded
		    upperClasses.add(owlClass);
	    }
	}

	// Afterwards, do the Path calculation algorithm starting from every root class
	// But before doing so, initialize the necessary variable for calculating
	// Breadth
	this.owlClassToDepth = new LinkedHashMap<Integer, Set<OWLClass>>();
	this.owlClassToDepth.put(0, rootClasses);
	for (OWLClass owlClass : rootClasses) {
	    depthBreadthCalculation(owlClass, 0);
	}
	
	int superClassesOfLeafClasses = 0;
	for (OWLClass owlClass : leafClasses) {
	    Collection<OWLClassExpression> superClassOfLeafClass = EntitySearcher.getSuperClasses(owlClass, ontology);
	    superClassesOfLeafClasses += OntologyUtility.classExpr2classes(superClassOfLeafClass).size();
	}
	
	returnObject.put("superClassesOfLeafClasses", superClassesOfLeafClasses);
	returnObject.put("PathsToLeafClasses", sumOfPathToLeafClasses);
	returnObject.put("Totaldepth", totalDepth);
	returnObject.put("Absoluteleafcardinality2", leafClasses.size());
	returnObject.put("Absoluterootcardinality2", upperClasses.size());
	Set<OWLClass> classesWithMoreThanOneDirectAncestor = getClassesWithAncestors(owlClasses, 2);
	returnObject.put("ClassesWithMoreThanOneAncestor2", classesWithMoreThanOneDirectAncestor.size());
	returnObject.put("SumOfDirectAncestorOfLeafClasses2", getAncestorClasses(leafClasses, 1).size());
	returnObject.put("SumOfDirectAncestorClasses2", getAncestorClasses(owlClasses, 1).size());
	returnObject.put("sumOfDirectAncestorWithMoreThanOneDirectAncestor2",
		getAncestorClasses(classesWithMoreThanOneDirectAncestor, 1).size());
	returnObject.put("Maxdepth", maxDepth);
	returnObject.put("minimumDepth", minDepth);

    }



    /**
     * Recursive method for calculating the number of Paths from the top of an
     * ontology to the bottom. Also calculates the Depth and Breath Metrics
     * 
     * @param currentDepth the current leven of the ontology depth. Starts with 0 at
     *                     the root classes. Helps the recursive function to
     *                     calculate maxDepth.
     * 
     * @param owlClass     The class where the search starts downwards
     */
    private void depthBreadthCalculation(OWLClass owlClass, int currentDepth) {
	totalDepth++; // Total Depth is counted per every level and every item

	currentDepth++;
	Collection<OWLClassExpression> subClassExpressions = EntitySearcher.getSubClasses(owlClass, ontology);
	Set<OWLClass> subclasses = (Set<OWLClass>) OntologyUtility.classExpr2classes(subClassExpressions);

	// the owlClassToDepth variable stores at which hierachy level an owlClass is
	// found. This enables, later on, the calculation of breath-metrics.
	if (!owlClassToDepth.containsKey(currentDepth))
	    owlClassToDepth.put(currentDepth, subclasses);
	else
	    owlClassToDepth.get(currentDepth).addAll(subclasses);

	// If a class has no further subclasses, we found a new path to a leaf class
	if (subclasses.size() == 0) {
	    sumOfPathToLeafClasses++;
	    if (currentDepth > this.maxDepth)
		maxDepth = currentDepth;
	    else if (currentDepth < minDepth)
		minDepth = currentDepth;
	}

	else {
	    for (OWLClass subclass : subclasses) {
		totalDepth++;
		this.depthBreadthCalculation(subclass, currentDepth);
	    }
	}
    }

    /***
     * Calculates the number Breath Metrics on the basis on the the owlCLassToDepth
     * variable. It is crucial to calculate the numberOfPaths method first!
     */
    private void calculateBreathMetrics() {
	int absoluteBreadth = 0;
	int maximalBreadth = 0;
	int minimumBreath = 500000000;
	int counter = 0;
	while (owlClassToDepth.containsKey(counter)) {
	    int levelSize = owlClassToDepth.get(counter).size();
	    if (levelSize > maximalBreadth)
		maximalBreadth = levelSize;
	    if (levelSize < minimumBreath && levelSize != 0)
		minimumBreath = levelSize;
	    absoluteBreadth += levelSize;
	    counter++;
	}
	returnObject.put("Absolutebreadth2", absoluteBreadth);
	returnObject.put("Maximalbreadth2", maximalBreadth);
	returnObject.put("minimumBreath", minimumBreath);
    }

    public void tanglednessMetric() {
	double tangledgness = 0.0;
	if (withImports)
	    tangledgness = OntologyUtility
		    .roundByGlobNK((double) parserI.getTangledClasses().size() / (double) parserI.getNoClasses());
	else
	    tangledgness = OntologyUtility
		    .roundByGlobNK((double) parser.getTangledClasses().size() / (double) parser.getNoClasses());

	returnObject.put("Tangledness", tangledgness);

    }

}
