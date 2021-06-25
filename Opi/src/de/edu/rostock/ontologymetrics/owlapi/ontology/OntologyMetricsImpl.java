package de.edu.rostock.ontologymetrics.owlapi.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.AnnotationAxiomsMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.BaseMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.ClassAxiomsMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.ClassMetrics;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.DataPropertyAxiomsMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.GraphMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.IndividualAxiomsMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.KnowledgebaseMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.ObjectPropertyAxiomsMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.OntologyMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.SchemaMetrics;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.GraphParser;

/**
 * This class represents the implementation of the interface
 * <code>OntologyMetrics</code>.
 * 
 */
public class OntologyMetricsImpl {

    private OWLOntology ontology;
    protected GraphParser parser;
    protected GraphParser parserI;

    private IRI iri;

    public OntologyMetricsImpl(OWLOntology pOntology) {
	if (pOntology != null) {
	    ontology = pOntology;
	    parser = new GraphParser(ontology, false); // without imports
	    parserI = new GraphParser(ontology, true); // with imports
	} else {
	    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	    try {
		ontology = manager.createOntology();
		parser = new GraphParser(ontology, false); // without imports
		parserI = new GraphParser(ontology, true); // with imports
	    } catch (OWLOntologyCreationException e) {
		System.out.println(e.getMessage());
		e.printStackTrace();
	    }
	}
	ontology = pOntology;
	iri = null;
    }

    /*
     * getter $ setter
     */

    public OWLOntology getOntology() {
	return ontology;
    }

    public void setOntology(OWLOntology pOntology) {
	ontology = pOntology;
    }

    public void setIRI(IRI pIRI) {
	iri = pIRI;
    }

    // neu
    public void setIRI(String pIRI) {
	iri = IRI.create(pIRI);
    }

    public IRI getIRI() {
	return iri;
    }

    // neu
    public void resetIRI() {
	iri = null;
    }

    /*
     * metrics
     */
    public Map<String, Object> getAllMetrics() {
	BaseMetric baseMetric = new BaseMetric();
	ClassAxiomsMetric classAxiomsMetric = new ClassAxiomsMetric();
	DataPropertyAxiomsMetric dataPropertyAxiomsMetric = new DataPropertyAxiomsMetric();
	IndividualAxiomsMetric individualAxiomsMetric = new IndividualAxiomsMetric();
	ObjectPropertyAxiomsMetric objectPropertyAxiomsMetric = new ObjectPropertyAxiomsMetric();
	AnnotationAxiomsMetric annotationAxiomsMetric = new AnnotationAxiomsMetric();
	GraphMetric graphMetric = new GraphMetric();

	Map<String, Object> resultSet = new HashMap<String, Object>();

	resultSet.put("BaseMetrics", baseMetric.calculateAllMetrics(ontology));
	resultSet.put("Classaxioms", classAxiomsMetric.calculateAllMetrics(ontology));
	resultSet.put("Datapropertyaxioms", dataPropertyAxiomsMetric.calculateAllMetrics(ontology));
	resultSet.put("Individualaxioms", individualAxiomsMetric.calculateAllMetrics(ontology));
	resultSet.put("Annotationaxioms", annotationAxiomsMetric.calculateAllMetrics(ontology));
	resultSet.put("Graphmetrics", graphMetric.calculateAllMetrics(ontology, parser, parserI));
	resultSet.put("Objectpropertyaxioms", objectPropertyAxiomsMetric.calculateAllMetrics(ontology));
	SchemaMetrics schemaMetric = new SchemaMetrics(baseMetric, classAxiomsMetric);
	KnowledgebaseMetric knowledgebaseMetric = new KnowledgebaseMetric(baseMetric);
	resultSet.put("SchemaMetrics", schemaMetric.calculateAllMetrics(ontology, parserI));
	resultSet.put("Knowledgebasemetrics", knowledgebaseMetric.calculateAllMetrics(ontology));
	Map<String, Object> wrapResult = new HashMap<String, Object>();
	wrapResult.put("BaseMetrics", resultSet);
	return wrapResult;
    }

    public Map<String, Object> getClassMetric(IRI iri) {
	ClassMetrics classMetrics = new ClassMetrics();
	classMetrics.calculateAllMetrics(ontology, iri)
    }
}