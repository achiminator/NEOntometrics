package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;

public class GraphParser {
	private Set<OWLClass> roots = new TreeSet<OWLClass>();
	private ArrayList<ArrayList<OWLClass>> paths = new ArrayList<ArrayList<OWLClass>>();
	private ArrayList<ArrayList<OWLClass>> pathsAll = new ArrayList<ArrayList<OWLClass>>();
	private ArrayList<Set<OWLClass>> generations = new ArrayList<Set<OWLClass>>();
	private Set<OWLClass> tangledClasses = new TreeSet<OWLClass>();
	private ArrayList<TreeSet<OWLClass>> sibs = new ArrayList<TreeSet<OWLClass>>();
	private Set<OWLClass> instancedClasses = new TreeSet<OWLClass>();
	private int noInstances = 0;
	private Set<Integer> instancesPerClass = new TreeSet<Integer>();
	private Set<OWLClass> attributedClasses = new TreeSet<OWLClass>();
	private int noAttributes = 0;
	private Set<Integer> attributesPerClass = new TreeSet<Integer>();
	private Set<OWLClass> relatedClasses = new TreeSet<OWLClass>();
	private int noRelations = 0;
	private Set<Integer> relationsPerClass = new TreeSet<Integer>();
	private int noClasses = 0;
	private Set<OWLClass> leaveClasses = new TreeSet<OWLClass>();
	private ArrayList<Set<OWLClass>> leaves = new ArrayList<Set<OWLClass>>();
	private int noInheritances = 0;
	private Set<OWLClass> passed = new TreeSet<OWLClass>();
	private Set<OWLClass> things = new TreeSet<OWLClass>();
	private ArrayList<TreeSet<OWLClass>> levels = new ArrayList<TreeSet<OWLClass>>();

	private String OntologyID = "";
	private String OntologyVersionID = "";
	private boolean I = false;

	Logger myLogger = Logger.getLogger(this.getClass());

	public GraphParser(OWLOntology o, boolean imports) {
		if (o == null)
			return;
		if (o.getOntologyID().getOntologyIRI() != null)
			OntologyID = o.getOntologyID().getOntologyIRI().toString();
		if (o.getOntologyID().getVersionIRI() != null)
			OntologyVersionID = o.getOntologyID().getVersionIRI().toString();

		I = imports;
		Set<OWLClass> set;

		// analyze with or without import closures
		if (I)
			set = o.getClassesInSignature(OntologyUtility.ImportClosures(true));
		else
			set = o.getClassesInSignature();

		noClasses = set.size();

		Iterator<OWLClass> i = set.iterator();
		OWLClass next;
		while (i.hasNext()) {
			next = i.next();

			// roots
			if (EntitySearcher.getSuperClasses(next, o).size() < 1)
				roots.add(next);

			// thing class
			if (next.isOWLThing()) {
				things.add(next);
			}
		}

		// root level
		levels.add((TreeSet<OWLClass>) roots);

		if (roots.size() > 0)
			pathing(new ArrayList<OWLClass>(), roots, o);
	}

	private void pathing(ArrayList<OWLClass> path, Set<OWLClass> siblings, OWLOntology o) {
		Iterator<OWLClass> i = siblings.iterator();
		OWLClass nextClass;
		while (i.hasNext()) {
			nextClass = i.next();

			pathsAll.add(path);

			Set<OWLClass> gen;
			// generations
			if (generations.size() <= path.size()) {
				gen = new TreeSet<OWLClass>();
				gen.add(nextClass);
				generations.add(gen);
			} else {
				gen = generations.get(path.size());
				if (!gen.contains(nextClass)) {
					gen.add(nextClass);
					generations.set(path.size(), gen);
				}
			}

			// childs (are siblings)
			TreeSet<OWLClass> childs = new TreeSet<OWLClass>();
			Iterator<OWLClassExpression> classLevels = EntitySearcher.getSubClasses(nextClass, o).iterator();

			while (classLevels.hasNext()) {
				childs.add(classLevels.next().asOWLClass());
			}

			if (!childs.isEmpty()) {
				levels.add(childs);
			}

			if (!passed.contains(nextClass)) {
				sibs.add(childs);
				noInheritances += childs.size();
				passed.add(nextClass);
			}

			// paths to leaves
			ArrayList<OWLClass> newpath = (ArrayList<OWLClass>) path.clone(); //BL 2016-09-08 de-referencing the current path
			
			boolean pathEnd = false;
			if (path.contains(nextClass)) {
				pathEnd = true;
			} else {
				newpath.add(nextClass);
				if (childs.size() < 1) {
					pathEnd = true;
					if (!leaveClasses.contains(nextClass))
						leaveClasses.add(nextClass);
				}
			}

			if (pathEnd) {
				paths.add(newpath);
				if (!leaveClasses.isEmpty()) {
					leaves.add(leaveClasses);
				}

			} else {
				pathing(newpath, childs, o);
			}

			// tangledness
			if (EntitySearcher.getSuperClasses(nextClass, o).size() > 1 && !tangledClasses.contains(nextClass))
				tangledClasses.add(nextClass);

			// instances
			if (EntitySearcher.getIndividuals(nextClass, o).size() > 0 && !instancedClasses.contains(nextClass)) {
				instancedClasses.add(nextClass);
				noInstances += EntitySearcher.getIndividuals(nextClass, o).size();
				instancesPerClass.add(new Integer(EntitySearcher.getIndividuals(nextClass, o).size()));
			}

			// attributes (Data Properties)
			if (nextClass.getDataPropertiesInSignature().size() > 0 && !attributedClasses.contains(nextClass)) {
				attributedClasses.add(nextClass);
				noAttributes += nextClass.getDataPropertiesInSignature().size();
				attributesPerClass.add(new Integer(nextClass.getDataPropertiesInSignature().size()));

			}

			// relations (Object Properties)
			if (nextClass.getObjectPropertiesInSignature().size() > 0 && !relatedClasses.contains(nextClass)) {
				relatedClasses.add(nextClass);
				noRelations += nextClass.getObjectPropertiesInSignature().size();
				relationsPerClass.add(new Integer(nextClass.getObjectPropertiesInSignature().size()));
			}
		}
	}

	public Set<OWLClass> getRoots() {
		return roots;
	}

	public Set<OWLClass> getThingClasses() {
		return things;
	}

	public ArrayList<ArrayList<OWLClass>> getPaths() {
		return paths;
	}

	public ArrayList<ArrayList<OWLClass>> getPathsAll() {
		return pathsAll;
	}

	public ArrayList<Set<OWLClass>> getGenerations() {
		return generations;
	}

	public ArrayList<Set<OWLClass>> getLeaves() {
		return leaves;
	}

	public Set<OWLClass> getTangledClasses() {
		return tangledClasses;
	}

	public ArrayList<TreeSet<OWLClass>> getSibs() {
		return sibs;
	}

	public ArrayList<TreeSet<OWLClass>> getLevels() {
		return levels;
	}

	public Set<OWLClass> getInstancedClasses() {
		return instancedClasses;
	}

	public int getNoInstances() {
		return noInstances;
	}

	public Set<Integer> getInstancesPerClass() {
		return instancesPerClass;
	}

	public Set<OWLClass> getAttributedClasses() {
		return attributedClasses;
	}

	public int getNoAttributes() {
		return noAttributes;
	}

	public Set<Integer> getAttributesPerClass() {
		return attributesPerClass;
	}

	public Set<OWLClass> getRelatedClasses() {
		return relatedClasses;
	}

	public int getNoRelations() {
		return noRelations;
	}

	public Set<Integer> getRelationsPerClass() {
		return relationsPerClass;
	}

	public int getNoClasses() {
		return noClasses;
	}

	public Set<OWLClass> getLeaveClasses() {
		return leaveClasses;
	}

	public int getNoInheritances() {
		return noInheritances;
	}

	public String getOntologyID() {
		return OntologyID;
	}

	public String getOntologyVersionID() {
		return OntologyVersionID;
	}
}
