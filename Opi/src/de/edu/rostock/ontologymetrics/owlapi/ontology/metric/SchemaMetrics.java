package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.GraphParser;


public class SchemaMetrics extends OntologyMetric {
    Map<String, Object> metrics;

    public SchemaMetrics(Map<String, Object> metrics) {
	this.metrics = metrics;
    }

    public Map<String, Object> getAllMetrics(OWLOntology ontology, GraphParser parser, GraphParser parserI) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("Attribute richness", attributeRichnessMetric(ontology, parserI));
	returnObject.put("Inheritance richness", schemaInheritenceRichnessMetric(metrics));
	returnObject.put("Relationship richness", schemaRelationshipRichnessMetric(metrics, ontology));
	returnObject.put("Attribute class ratio", attributeClassRatio(metrics, ontology));
	returnObject.put("Attribute class ratio", attributeClassRatio(metrics, ontology));
	returnObject.put("Equivalence ratio", equivalenceRatioMetric(metrics, ontology));
	returnObject.put("Axiom/class ratio", axiomClassRatioMetric(metrics));
	returnObject.put("Inverse relations ratio", inverseRelationsRatioMetric(ontology));
	returnObject.put("Class/relation ratio", classRelationsRatioMetric(metrics, ontology));

	return returnObject;
    }

    // TODO: Restructure for pure read
    public double attributeRichnessMetric(OWLOntology ontology, GraphParser parserI) {
	double ret = 0;
	float classes = parserI.getNoClasses();
	int datapropertycount = ontology.getDataPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();

	if (classes == 0) {
	    ret = 0f;
	} else {
	    ret = (double) datapropertycount / (double) classes;
	    myLogger.debug("datapropertycount/classes="
		    + datapropertycount
		    + "/"
		    + classes);
	}

	return OntologyUtility.roundByGlobNK(ret);
    }

    /**
     * the inheritance richness of the schema (IRs) is defined as the average number
     * of subclasses per class
     * 
     * @return Metric Value
     */
    public double schemaInheritenceRichnessMetric(Map<String, Object> metrics) {
	double countSubClasses = (double) metrics.get("SubClassOf axioms count");
	double countClasses = (double) metrics.get("Total classes count");

	// avoid a division by zero
	if (countClasses == 0) {
	    return 0.0;
	} else {
	    myLogger.debug("countSubClasses: "
		    + countSubClasses);
	    myLogger.debug("countClasses: "
		    + countClasses);
	    myLogger.debug("countSubClasses / countClasses: "
		    + countSubClasses / countClasses);
	    double ret = countSubClasses / countClasses;
	    return OntologyUtility.roundByGlobNK(ret);
	}
    }

    // TODO: Replace things here by local variants
    public double schemaRelationshipRichnessMetric(Map<String, Object> metrics, OWLOntology ontology) {
	double inheritedRelationships = (double) metrics.get("SubClassOf axioms count");
	double objectPropertyCount = ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true))
		.size();
	double equivalentClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES,
		OntologyUtility.ImportClosures(true));
	double disjointClasses = ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES,
		OntologyUtility.ImportClosures(true));

	double nonInheritedRelationships = objectPropertyCount + equivalentClasses + disjointClasses;// + classAssertion
												     // + sameIndividual
												     // +
												     // differentIndividual;

	// nonInheritedRelationships= countRelationships;
	// avoid a division by zero
	if (nonInheritedRelationships + inheritedRelationships == 0) {
	    return 0.0;
	} else {
	    return OntologyUtility
		    .roundByGlobNK(nonInheritedRelationships / (nonInheritedRelationships + inheritedRelationships));
	}
    }

    public double attributeClassRatio(Map<String, Object> metrics, OWLOntology ontology) {
	double classCount = (double) metrics.get("Total classes count");
	double classesWithAttributes = 0;
	double result = 0;

	myLogger.debug("classes: "
		+ classCount);

	// new
	// for (OWLClass cls : ontology.getClassesInSignature(true)) {
	for (OWLClass cls : ontology.getClassesInSignature(OntologyUtility.ImportClosures(true))) { // new

	    Collection<OWLClassExpression> superClses = EntitySearcher.getSuperClasses(cls, ontology);
	    for (OWLClassExpression superCls : superClses) {
		if (superCls.isAnonymous()) {
		    classesWithAttributes++;
		    continue;
		}
	    }
	}
	if (classCount == 0) {
	    return 0.0;
	}

	result = classesWithAttributes / classCount;

	myLogger.debug("result: "
		+ result);
	return OntologyUtility.roundByGlobNK(result);
    }

    public double equivalenceRatioMetric(Map<String, Object> metrics, OWLOntology ontology) {
	double classes = (double) metrics.get("Total classes count");
	// MLIC: changed getAxiomCount with boolean is deprecated
	// float sameClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES,
	// true);
	double sameClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES, OntologyUtility.ImportClosures(true));

	if (classes == 0) {
	    return 0.0;
	} else {
	    return OntologyUtility.roundByGlobNK(sameClasses / classes);
	}
    }

    public double axiomClassRatioMetric(Map<String, Object> metrics) {
	double axioms = (double) metrics.get("Axioms");
	double classes = (double) metrics.get("Total Classes count");
	if (classes == 0) {
	    return 0.0;
	} else {
	    return OntologyUtility.roundByGlobNK(axioms / classes);
	}
    }

    public double inverseRelationsRatioMetric(OWLOntology ontology) {
	double objectpropertycount = ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true))
		.size();
	double inverseobjectproperties = ontology.getAxiomCount(AxiomType.INVERSE_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(true));
	double inversefunctionalproperties = ontology.getAxiomCount(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(true));
	double functionaldatapropertycount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_DATA_PROPERTY,
		OntologyUtility.ImportClosures(true));
	if (objectpropertycount + functionaldatapropertycount == 0) {
	    return 0.0;
	} else {
	    return OntologyUtility.roundByGlobNK((inverseobjectproperties + inversefunctionalproperties)
		    / (objectpropertycount + functionaldatapropertycount));
	}
    }

    public double classRelationsRatioMetric(Map<String, Object> metrics, OWLOntology ontology) {
	double classes = (double) metrics.get("Total classes count");
	double subClasses = (double) metrics.get("SubClassOf axioms count");
	double objectPropertyCount = ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true))
		.size();
	double equivalentClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES,
		OntologyUtility.ImportClosures(true));
	double disjointClasses = ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES,
		OntologyUtility.ImportClosures(true));
	double relations = objectPropertyCount + equivalentClasses + disjointClasses + subClasses;// + classAssertion +
												  // sameIndividual +
												  // differentIndividual;
	// relations=countRelationships;
	if (relations == 0) {
	    return 0.0;
	} else {
	    return OntologyUtility.roundByGlobNK(classes / relations);
	}
    }
}
