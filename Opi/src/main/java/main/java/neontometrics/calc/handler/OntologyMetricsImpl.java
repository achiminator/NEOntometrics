package main.java.neontometrics.calc.handler;


import java.util.LinkedHashMap;

import java.util.Map;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import main.java.neontometrics.calc.AnnotationAxiomsMetric;
import main.java.neontometrics.calc.BaseMetric;
import main.java.neontometrics.calc.ClassAxiomsMetric;
import main.java.neontometrics.calc.DataPropertyAxiomsMetric;
import main.java.neontometrics.calc.GraphMetric;
import main.java.neontometrics.calc.IndividualAxiomsMetric;
import main.java.neontometrics.calc.ObjectPropertyAxiomsMetric;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * This class represents the implementation of the interface
 * <code>OntologyMetrics</code>.
 * 
 */
public class OntologyMetricsImpl {

    private OWLOntology ontology;
    

    protected Future<BaseMetric> baseMetric;
    protected Future<ClassAxiomsMetric> classAxiomsMetric;
    protected Future<DataPropertyAxiomsMetric> dataPropertyAxiomsMetric;
    protected Future<IndividualAxiomsMetric> individualAxiomsMetric;
    protected Future<ObjectPropertyAxiomsMetric> objectPropertyAxiomsMetric;
    protected Future<AnnotationAxiomsMetric> annotationAxiomsMetric;
    protected Future<GraphMetric> graphMetric;
    private IRI iri;

    public OntologyMetricsImpl(OWLOntology pOntology) {
	if (pOntology != null) {
	    ontology = pOntology;

	} else {
	    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	    try {
		ontology = manager.createOntology();
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
    public Map<String, Object> getAllMetrics(boolean reasonerCalculationSelected) throws InterruptedException, ExecutionException {

	Map<String, Object> resultSet;
	Configuration config = new Configuration();
	config.ignoreUnsupportedDatatypes = true;
	if(reasonerCalculationSelected) {
	    OWLReasoner reasoner = new Reasoner(config, ontology);
	    InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner);
	    iog.fillOntology(new OWLDataFactoryImpl(),  ontology);
	    resultSet = execMetricCalculation(true);
	    resultSet.put("reasonerActive", true);
	    resultSet.put("consistencyCheckSuccessful", reasoner.isConsistent());
	}
	else {
	    resultSet = execMetricCalculation(true);
	    resultSet.put("reasonerActive", false);
	    resultSet.put("consistencyCheckSuccessful", "False");
	}


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

	graphMetric = service.submit(new GraphMetric(ontology, imports));

	// resultSet.put("Basemetrics", baseMetric.calculateAllMetrics(ontology));

	resultSet.putAll(graphMetric.get().getReturnObject());
	service.shutdown();
	return resultSet;
    }

//    public List<Map<String, Object>> getClassMetrics() throws Exception {
//	ExecutorService service = Executors.newWorkStealingPool();
//
//	List<Map<String, Object>> ClassMetricsList = new ArrayList<Map<String, Object>>();
//	List<Future<ClassMetrics>> tmpList = new ArrayList<>();
//	Set<OWLClass> om = ontology.getClassesInSignature();
//	for (OWLClass owlClass : om) {
//	    //tmpList.add(service.submit(
//	    //new ClassMetrics(knowledgebaseMetric.get(), baseMetric.get(), ontology, owlClass.getIRI())));
//	}
//	service.shutdown();
//	service.awaitTermination(2, TimeUnit.HOURS);
//	for (Future<ClassMetrics> future : tmpList) {
//	    ClassMetricsList.add(future.get().getAllMetrics());
//	}
//	return ClassMetricsList;
//
//    }
}