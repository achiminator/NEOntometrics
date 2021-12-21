package de.edu.rostock.ontologymetrics.owlapi.ontology;

import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;

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
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * This class represents the implementation of the interface
 * <code>OntologyMetrics</code>.
 * 
 */
public class OntologyMetricsImpl {

    private OWLOntology ontology;
    protected GraphParser parser;
    protected GraphParser parserI;
    
    protected Future<BaseMetric> baseMetric;
    protected Future<ClassAxiomsMetric> classAxiomsMetric;
    protected Future<DataPropertyAxiomsMetric> dataPropertyAxiomsMetric;
    protected Future<IndividualAxiomsMetric> individualAxiomsMetric;
    protected Future<ObjectPropertyAxiomsMetric> objectPropertyAxiomsMetric;
    protected Future<AnnotationAxiomsMetric> annotationAxiomsMetric;
    protected Future<GraphMetric> graphMetric;
    protected Future<SchemaMetrics> schemaMetric;
    protected Future<KnowledgebaseMetric> knowledgebaseMetric;
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
    public Map<String, Object> getAllMetrics() throws InterruptedException, ExecutionException {


	Configuration config = new Configuration();
	config.ignoreUnsupportedDatatypes = true;
	OWLReasoner reasoner = new Reasoner(config, ontology);
	InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner);
	//iog.fillOntology(new OWLDataFactoryImpl(),  ontology);
	Map<String, Object> resultSet = execMetricCalculation(true);
	Map<String, Object> wrapResult = new LinkedHashMap<String, Object>();
	wrapResult.put("GeneralOntologyMetrics", resultSet);
	return wrapResult;
    }

    protected Map<String, Object> execMetricCalculation(boolean imports) throws InterruptedException, ExecutionException {
	Map<String, Object> resultSet = new LinkedHashMap<String, Object>();
	ExecutorService service = Executors.newWorkStealingPool();
	baseMetric = service.submit(new BaseMetric(ontology, imports));
	classAxiomsMetric = service.submit(new ClassAxiomsMetric(ontology,imports));
	objectPropertyAxiomsMetric = service.submit(new ObjectPropertyAxiomsMetric(ontology,imports));
	dataPropertyAxiomsMetric = service.submit(new DataPropertyAxiomsMetric(ontology,imports));
	individualAxiomsMetric = service.submit(new IndividualAxiomsMetric(ontology,imports));
	annotationAxiomsMetric = service.submit(new AnnotationAxiomsMetric(ontology, imports));
	resultSet.putAll(classAxiomsMetric.get().getAllMetrics());
	resultSet.putAll(objectPropertyAxiomsMetric.get().getReturnObject());
	resultSet.putAll(dataPropertyAxiomsMetric.get().getReturnObject());
	resultSet.putAll(individualAxiomsMetric.get().getReturnObject());
	resultSet.putAll(annotationAxiomsMetric.get().getReturnObject());
	resultSet.putAll(baseMetric.get().getReturnObject());
	//The following metric calculations now need some of the previous ones. As the previous calculations are stored in the 
	schemaMetric = service.submit(new SchemaMetrics(resultSet, imports, ontology));
	knowledgebaseMetric = service.submit(new KnowledgebaseMetric(resultSet, ontology, imports));
	graphMetric = service.submit(new GraphMetric(ontology, parser, parserI, imports));
	
	// resultSet.put("Basemetrics", baseMetric.calculateAllMetrics(ontology));

	resultSet.putAll(schemaMetric.get().getReturnObject());
	resultSet.putAll(knowledgebaseMetric.get().getReturnObject());
	resultSet.putAll(graphMetric.get().getReturnObject());
	service.shutdown();
	return resultSet;
    }

    public List<Map<String, Object>> getClassMetrics() throws Exception {
	if (schemaMetric == null || knowledgebaseMetric == null)
	    throw new Exception("Schema and KnowledgebaseMetrics must be run first");
	ExecutorService service = Executors.newWorkStealingPool();

	List<Map<String, Object>> ClassMetricsList = new ArrayList<Map<String, Object>>();
	List<Future<ClassMetrics>> tmpList = new ArrayList<>();
	Set<OWLClass> om = ontology.getClassesInSignature();
	for (OWLClass owlClass : om) {
	    tmpList.add(service.submit(
		    new ClassMetrics(knowledgebaseMetric.get(), baseMetric.get(), ontology, owlClass.getIRI())));
	}
	service.shutdown();
	service.awaitTermination(2, TimeUnit.HOURS);
	for (Future<ClassMetrics> future : tmpList) {
	    ClassMetricsList.add(future.get().getAllMetrics());
	}
	return ClassMetricsList;

    }
}