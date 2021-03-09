package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.GraphParser;

public abstract class OntologyMetric {

	protected OWLOntology ontology;

	protected String label;
	
	//MLIC: new for graph metrics
	protected GraphParser parser;
	protected GraphParser parserI;

	final Logger myLogger = Logger.getLogger(this.getClass());

	public OntologyMetric(OWLOntology pOntology) {
		if (pOntology != null) {
			ontology = pOntology;
			parser = new GraphParser(ontology, false); //without imports
			parserI = new GraphParser(ontology, true);  //with imports
		} else {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			myLogger.debug("Manager angelegt" + manager.toString());
			try {
				ontology = manager.createOntology();
				myLogger.debug("Ontology angelegt" + ontology.toString());
				parser = new GraphParser(ontology, false); //without imports
				parserI = new GraphParser(ontology, true);  //with imports
			} catch (OWLOntologyCreationException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public abstract Object getValue();

	public abstract String getLabel();

	// getter $ setter

	public OWLOntology getOntology() {
		return ontology;
	}

}
