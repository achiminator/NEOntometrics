package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.knowledgebasemetric;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountTotalClassesMetric;

public class ClassRichnessMetric extends KnowledgebaseMetric {

	Logger myLogger = Logger.getLogger(this.getClass());

	public ClassRichnessMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Double getValue() {
		double countClassHasIndividual = 0.0;
		int totalClasses = new CountTotalClassesMetric(ontology).getValue();
		double countTotalClasses = totalClasses;

		// avoid a division by zero
		if (countTotalClasses == 0) {
			return 0.0;
		} else {
			// TODO keine Vergleichswerte! Mit oder ohne Imports?
			for (OWLClass owlClass : ontology.getClassesInSignature(OntologyUtility.ImportClosures(true))) {

				// MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
				if (!EntitySearcher.getIndividuals(owlClass, ontology).isEmpty()) {

					countClassHasIndividual++;
				}
			}
			
			myLogger.debug("totalClasses: " + totalClasses);
			myLogger.debug("countClassHasIndividual: " + countClassHasIndividual);
			myLogger.debug("countTotalClasses: " + countTotalClasses);
			
			return OntologyUtility.roundByGlobNK((double) countClassHasIndividual / (double) countTotalClasses);
		}
	}

	@Override
	public String toString() {
		return "Class richness";
	}

}
