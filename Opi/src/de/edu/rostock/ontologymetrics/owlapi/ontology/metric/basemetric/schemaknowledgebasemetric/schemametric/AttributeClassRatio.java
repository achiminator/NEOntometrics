package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.schemametric;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLDataProperty;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountTotalClassesMetric;

public class AttributeClassRatio extends SchemaMetric {

	Logger myLogger = Logger.getLogger(this.getClass());

	public AttributeClassRatio(OWLOntology pOntology) {
		super(pOntology);

	}

	public Double getValue() {
		double classes = new CountTotalClassesMetric(ontology).getValue();
		double classesWithAttributes = 0;
		double result = 0;

		myLogger.debug("classes: " + classes);

		// new
		// for (OWLClass cls : ontology.getClassesInSignature(true)) {
		for (OWLClass cls : ontology.getClassesInSignature(OntologyUtility.ImportClosures(true))) { // new
		System.out.println(cls.getIRI());
		System.out.println(cls.getNestedClassExpressions());
		System.out.println(cls.getDataPropertiesInSignature());
		    for (OWLDataProperty dataProperty : cls.getDataPropertiesInSignature()) {
				// exklusiv-oder
				if (dataProperty.isOWLDataProperty() ^ dataProperty.isOWLObjectProperty()) {
					classesWithAttributes++;
				}

			}
		}
		if (classes == 0) {
			return 0.0;
		}

		result = classesWithAttributes / classes;

		myLogger.debug("result: " + result);
		return OntologyUtility.roundByGlobNK(result);
	}

	public String toString() {
		return "Attribute class ratio";
	}

}
