package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.GraphParser;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class GraphMetric extends MetricCalculations implements Callable<GraphMetric> {

	private static final String False = null;
	private GraphParser parser;
	private GraphParser parserI;
	private boolean withImports;

	private int sumOfPathToLeafClasses = 0;
	private int totalDepth = 0;
	private int maxDepth = 0;
	private int minDepth = 999990;
	private LinkedHashMap<Integer, Set<OWLClass>> owlClassToDepth; // Links the classes in the ontology to a hierachy
	private int numberOfCircles = 0;
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

// TODO: Remove 
	private void absoluteSiblingCardinalityMetric() {
		int absoluteSibblingCardinality;
		if (withImports)
			absoluteSibblingCardinality = parserI.getSibs().size();
		else
			absoluteSibblingCardinality = parser.getSibs().size();
		returnObject.put("absoluteSiblingCardinality", absoluteSibblingCardinality);

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

		// Container for all leaf Classes of this ontology (Such without other
		// subclasses)
		Set<OWLClass> leafClasses = new TreeSet<OWLClass>();

		// stores the relation of the leaf classes to their parents. Used to calculate
		// Max Fanoutness of leaf classes.
		Map<OWLClass, List<OWLClass>> parentsLeafRelation = new HashMap<OWLClass, List<OWLClass>>();

		// Container for the opposite, for classes (such that have leaves)
		Set<OWLClass> upperClasses = new TreeSet<OWLClass>();

		// Root classes are at the very top of the Ontology
		Set<OWLClass> rootClasses = new TreeSet<OWLClass>();

		Set<OWLClass> owlClasses = ontology.getClassesInSignature(OntologyUtility.ImportClosures(withImports));

		// Iterate over all classes
		for (OWLClass owlClass : owlClasses) {

			// If a thing does not have superclasses, then it is a root class
			Collection<OWLClass> superClassesofOwlClass = OntologyUtility
					.classExpr2classes(EntitySearcher.getSuperClasses(owlClass, ontology));
			if ((superClassesofOwlClass.size() < 1 // add a class as a root class if it does not have any superclasses
					|| superClassesofOwlClass.contains(new OWLDataFactoryImpl().getOWLThing())) // or if it is a subclass
																								// of OWL:Thing
							&& !owlClass.isOWLThing()) // And it is not owl:Thing itself!
				rootClasses.add(owlClass);
			Collection<OWLClassExpression> subClassExpr = EntitySearcher.getSubClasses(owlClass, ontology);
			Collection<OWLClass> subClasses = OntologyUtility.classExpr2classes(subClassExpr);
			if (subClasses.size() < 1) {
				// If a classes does not have any subclasses, it is a leaf class. As owl-Thing
				// is always on top, we do not count it as a root class.
				leafClasses.add(owlClass);

				// This section sets up the relations between the leaf classes and their parents
				// (parents ->List<LeafClasses>)
				Collection<OWLClass> parentsOfThisLeafClass = OntologyUtility
						.classExpr2classes(EntitySearcher.getSuperClasses(owlClass, ontology));
				for (OWLClass parentOfThisLeafClass : parentsOfThisLeafClass) {
					if (!parentsLeafRelation.containsKey(parentOfThisLeafClass))
						parentsLeafRelation.put(parentOfThisLeafClass, new ArrayList<OWLClass>());
					parentsLeafRelation.get(parentOfThisLeafClass).add(owlClass);
				}
			} else {
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
			depthBreadthCalculation(new HashSet<OWLClass>(), owlClass, 0);
		}
		returnObject.put("numberOfConnectedGraphs", numberOfConnectedGraphs(rootClasses));

		// This measurement measures what is the biggest amount of leaf classes anchored
		// to a parent
		int maxFanOutnessOfLeafClass = 0;
		for (OWLClass owlClass : parentsLeafRelation.keySet()) {
			if (parentsLeafRelation.get(owlClass).size() > maxFanOutnessOfLeafClass)
				maxFanOutnessOfLeafClass = parentsLeafRelation.get(owlClass).size();
		}

		int superClassesOfLeafClasses = 0;
		for (OWLClass owlClass : leafClasses) {
			Collection<OWLClassExpression> superClassOfLeafClass = EntitySearcher.getSuperClasses(owlClass, ontology);
			superClassesOfLeafClasses += OntologyUtility.classExpr2classes(superClassOfLeafClass).size();
		}
		returnObject.put("circleHierachies", numberOfCircles);
		returnObject.put("superClassesOfLeafClasses", superClassesOfLeafClasses);
		returnObject.put("pathsToLeafClasses", sumOfPathToLeafClasses);
		returnObject.put("absoluteDepth", totalDepth);
		returnObject.put("absoluteLeafCardinality", leafClasses.size());
		returnObject.put("absoluteRootCardinality", upperClasses.size());
		Set<OWLClass> classesWithMoreThanOneDirectAncestor = getClassesWithAncestors(owlClasses, 2);
		returnObject.put("classesWithMoreThanOneAncestor", classesWithMoreThanOneDirectAncestor.size());
		returnObject.put("sumOfDirectAncestorOfLeafClasses", getAncestorClasses(leafClasses, 1).size());
		returnObject.put("sumOfDirectAncestorClasses", getAncestorClasses(owlClasses, 1).size());
		returnObject.put("sumOfDirectAncestorsWithMoreThanOneDirectAncestor",
				getAncestorClasses(classesWithMoreThanOneDirectAncestor, 1).size());
		returnObject.put("maximalDepth", maxDepth);
		returnObject.put("minimumDepth", minDepth);
		returnObject.put("maxFanoutnessOfLeafClasses", maxFanOutnessOfLeafClass);
		returnObject.put("rootClasses", rootClasses.size());

	}

	/**
	 * Checks if the classes/subclasses of the root classes are linked together or
	 * not. (By sub class or object property declarations)
	 * 
	 * @param rootClasses
	 * @return
	 */
	private int numberOfConnectedGraphs(Set<OWLClass> rootClasses) {
		Map<Integer, Set<OWLClass>> relatedClassesMapping = new HashMap<Integer, Set<OWLClass>>();
		int i = 1;
		for (OWLClass rootClass : rootClasses) {
			Set<OWLClass> subClassesOfRootClass = allSubClassesOfClass(new HashSet<OWLClass>(), rootClass);
			Set<OWLClass> relatedClassesWithRootClass = new HashSet<OWLClass>();
			relatedClassesWithRootClass.addAll(getConnectedClasses(rootClass));
			for (OWLClass owlClass : subClassesOfRootClass) {
				relatedClassesWithRootClass.add(owlClass);
				relatedClassesWithRootClass.addAll(getConnectedClasses(owlClass));
			}
			if (relatedClassesWithRootClass.size() < 1 || rootClass.isOWLThing())
				continue;
			if (!relatedClassesMapping.containsKey(1)) // If the class does not have the first element yet, add all the
														// subclasses to it
				relatedClassesMapping.put(i, relatedClassesWithRootClass);
			else {
				int elementIncluded = -1;
				for (Integer mapId : relatedClassesMapping.keySet()) {
					for (OWLClass subClass : relatedClassesWithRootClass) {
						if (relatedClassesMapping.get(mapId).contains(subClass)) {
							elementIncluded = mapId;
							break;
						}
					}
				}
				if (elementIncluded > 0)
					relatedClassesMapping.get(elementIncluded).addAll(relatedClassesWithRootClass);
				else {
					relatedClassesMapping.put(relatedClassesMapping.size() + 1, relatedClassesWithRootClass);
				}
			}
		}
		return relatedClassesMapping.size();
	}

	/***
	 * Get the such classes of a given class that are related with it through its
	 * object properties
	 * 
	 * @param owlClass
	 * @return Set of related classes
	 */
	private Set<OWLClass> getConnectedClasses(OWLClass owlClass) {
		Collection<OWLClassExpression> connectedRelationsOfSubClass = EntitySearcher.getSuperClasses(owlClass,
				ontology);
		Set<OWLClass> connectedClasses = new HashSet<OWLClass>();
		for (OWLClassExpression classExpr : connectedRelationsOfSubClass) {
			connectedClasses.addAll(classExpr.getClassesInSignature());
		}
		return connectedClasses;
	}

	/**
	 * Returns a Set of all subclasses of a given class
	 * 
	 * @param A         set of the items that the class has already detected (needed
	 *                  for recursion)
	 * @param rootClass
	 * @return
	 */
	private Set<OWLClass> allSubClassesOfClass(Set<OWLClass> alreadyDetected, OWLClass rootClass) {
		Set<OWLClass> subClasses = (Set<OWLClass>) OntologyUtility
				.classExpr2classes(EntitySearcher.getSubClasses(rootClass, ontology));
		if (subClasses.size() > 0 && !alreadyDetected.contains(rootClass)) // the latter is necessary to prevent the
																			// stack overflow in cyclic relationships
			for (OWLClass owlClass : subClasses) {
				subClasses.addAll(allSubClassesOfClass(subClasses, owlClass));
			}
		return subClasses;
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
	private void depthBreadthCalculation(Set<OWLClass> alreadyChecked, OWLClass owlClass, int currentDepth) {
		totalDepth++; // Total Depth is counted per every level and every item
		if (alreadyChecked.contains(owlClass)) {
			numberOfCircles++;
			return;
		}
		alreadyChecked.add(owlClass);

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
				this.depthBreadthCalculation(alreadyChecked, subclass, currentDepth);
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
		returnObject.put("absoluteBreadth", absoluteBreadth);
		returnObject.put("maximalBreadth", maximalBreadth);
		returnObject.put("minimumBreath", minimumBreath);
	}

//TODO: Switch to class without parser 
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
