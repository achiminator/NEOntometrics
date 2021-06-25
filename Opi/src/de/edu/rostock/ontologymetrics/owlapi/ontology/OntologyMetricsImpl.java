package de.edu.rostock.ontologymetrics.owlapi.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
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
    protected BaseMetric baseMetric;;
    protected ClassAxiomsMetric classAxiomsMetric;
    protected DataPropertyAxiomsMetric dataPropertyAxiomsMetric;
    protected IndividualAxiomsMetric individualAxiomsMetric;
    protected ObjectPropertyAxiomsMetric objectPropertyAxiomsMetric;
    protected AnnotationAxiomsMetric annotationAxiomsMetric;
    protected GraphMetric graphMetric = new GraphMetric();
    protected SchemaMetrics schemaMetric;
    protected KnowledgebaseMetric knowledgebaseMetric;
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
	baseMetric = new BaseMetric();
	classAxiomsMetric = new ClassAxiomsMetric();
	dataPropertyAxiomsMetric = new DataPropertyAxiomsMetric();
	individualAxiomsMetric = new IndividualAxiomsMetric();
	objectPropertyAxiomsMetric = new ObjectPropertyAxiomsMetric();
	annotationAxiomsMetric = new AnnotationAxiomsMetric();
	graphMetric = new GraphMetric();

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

	Map<String, Object> resultSet = new LinkedHashMap<String, Object>();

	resultSet.put("Basemetrics", baseMetric.calculateAllMetrics(ontology));
	resultSet.put("Classaxioms", classAxiomsMetric.calculateAllMetrics(ontology));
	resultSet.put("Objectpropertyaxioms", objectPropertyAxiomsMetric.calculateAllMetrics(ontology));
	resultSet.put("Datapropertyaxioms", dataPropertyAxiomsMetric.calculateAllMetrics(ontology));
	resultSet.put("Individualaxioms", individualAxiomsMetric.calculateAllMetrics(ontology));
	resultSet.put("Annotationaxioms", annotationAxiomsMetric.calculateAllMetrics(ontology));
	schemaMetric = new SchemaMetrics(baseMetric, classAxiomsMetric);
	knowledgebaseMetric = new KnowledgebaseMetric(baseMetric);
	resultSet.put("Schemametrics", schemaMetric.calculateAllMetrics(ontology, parserI));
	resultSet.put("Knowledgebasemetrics", knowledgebaseMetric.calculateAllMetrics(ontology));
	resultSet.put("Graphmetrics", graphMetric.calculateAllMetrics(ontology, parser, parserI));

	Map<String, Object> wrapResult = new LinkedHashMap<String, Object>();
	wrapResult.put("BaseMetrics", resultSet);
	return wrapResult;
    }

    public List<Map<String, Object>> getClassMetrics() throws Exception {
	if (schemaMetric == null || knowledgebaseMetric == null)
	    throw new Exception("Schema and KnowledgebaseMetrics must be run first");
	ClassMetrics classMetrics = new ClassMetrics(knowledgebaseMetric, baseMetric);

	List<Map<String, Object>> ClassMetricsList = new ArrayList<Map<String, Object>>();
	Set<OWLClass> om = ontology.getClassesInSignature();
	for (OWLClass owlClass : om) {
	    ClassMetricsList.add(classMetrics.calculateAllMetrics(ontology, owlClass.getIRI()));
	}
	return ClassMetricsList;
    }
}