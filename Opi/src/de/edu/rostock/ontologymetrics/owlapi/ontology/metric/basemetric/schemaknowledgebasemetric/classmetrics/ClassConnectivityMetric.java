package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.classmetrics;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import com.google.common.collect.Multimap;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class ClassConnectivityMetric extends ClassMetrics {

	Logger myLogger = Logger.getLogger(this.getClass());

	public ClassConnectivityMetric(OWLOntology pOntology, IRI pIri) {
		super(pOntology, pIri);
		myLogger.info("pIri: " + pIri);
	}

	@Override
	/**
	 * Formally, the connectivity (Cn) of a class Ci is defined as the number of
	 * instances of other classes that are connected to instances of that class
	 * (Ij).
	 */
	public Integer getValue() {
		// TODO getConnectivity?! TEST

		int noInheritances = 0;

		//Instances of that class
		Iterator<OWLIndividual> cInstances = EntitySearcher
				.getInstances(OntologyUtility.getClass(ontology, getClassIRI()), ontology).iterator();

		while (cInstances.hasNext()) {
			//connection to others
			Multimap<OWLObjectPropertyExpression, OWLIndividual> same = EntitySearcher.getObjectPropertyValues(cInstances.next(), ontology);
			
			for (OWLObjectPropertyExpression key : same.keySet()) {
		        Collection<OWLIndividual> values = same.get(key);
		        for (@SuppressWarnings("unused") OWLIndividual value : values) {
		        	noInheritances++;
			    }
		    }
		}
		return noInheritances;
	}

	@Override
	public String toString() {
		return "Class connectivity";
	}

}
