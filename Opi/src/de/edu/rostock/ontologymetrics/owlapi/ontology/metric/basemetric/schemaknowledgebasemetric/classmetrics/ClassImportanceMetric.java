package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.classmetrics;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountTotalIndividualsMetric;

public class ClassImportanceMetric extends ClassMetrics {

	Logger myLogger = Logger.getLogger(this.getClass());
	
	public ClassImportanceMetric(OWLOntology pOntology, IRI iri) {
		super(pOntology, iri);
	}

	@Override
	public Double getValue() {
		int countTotalInstances = new CountTotalIndividualsMetric(ontology).getValue();
		OWLClass cls = OntologyUtility.getClass(ontology, getClassIRI());
		int countInstances = countTotalInstancesOf(cls);

		// avoid a division by zero
		if (countTotalInstances == 0) {
			return 0.0;
		} else {
			return OntologyUtility.roundByGlobNK((double) countInstances / (double) countTotalInstances);
		}
	}

	@Override
	public String toString() {
		return "Class importance";
	}

}
