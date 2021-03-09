package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.schemametric;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountTotalClassesMetric;

public class AttributeClassRatio extends SchemaMetric {

    Logger myLogger = Logger.getLogger(this.getClass());

    public AttributeClassRatio(OWLOntology pOntology) {
	super(pOntology);

    }

    public Double getValue() {
	double classCount = new CountTotalClassesMetric(ontology).getValue();
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

    public String toString() {
	return "Attribute class ratio";
    }

}
