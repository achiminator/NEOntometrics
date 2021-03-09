package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.classmetrics;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.knowledgebasemetric.AveragePopulationMetric;

/**
 * Return the actual number of instances that belong to the subtree rooted at
 * <code>cls</code> divided by the expected number of instances that belong to
 * the subtree rooted at
 */
public class ClassFulnessMetric extends ClassMetrics {

	Logger myLogger = Logger.getLogger(this.getClass());

	public ClassFulnessMetric(OWLOntology pOntology, IRI pIri) {
		super(pOntology, pIri);
	}

	@Override
	public Double getValue() {
		Integer numberOfSubClasses = new CountClassChildrenMetric(ontology, getClassIRI()).getValue();
		Integer classInstances = new CountClassInstancesMetric(ontology, getClassIRI()).getValue();
		double actualNumberOfInstances = 0.0;

		if (numberOfSubClasses > 0)
			actualNumberOfInstances = ((double) classInstances / (double) numberOfSubClasses);

		double expectedNumberOfInstances = new AveragePopulationMetric(ontology).getValue();

		// avoid a division by zero
		if (expectedNumberOfInstances > 0) {
			return 0.0;
		} else {
			return OntologyUtility.roundByGlobNK((actualNumberOfInstances / expectedNumberOfInstances));
		}
	}

	@Override
	public String toString() {
		return "Class fulness";
	}

}
