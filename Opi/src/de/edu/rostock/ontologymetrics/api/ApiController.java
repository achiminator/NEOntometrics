package de.edu.rostock.ontologymetrics.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
// webservices
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//xml

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

import de.edu.rostock.ontologymetrics.api.error.InvalidOntologyException;
import de.edu.rostock.ontologymetrics.api.error.WrongURIException;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyMetricManagerImpl;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.ClassMetrics;
import javassist.ClassMap;

@Path("")
public class ApiController {

    final Logger myLogger = Logger.getLogger(this.getClass());

    public ApiController() {
	// TODO Auto-generated constructor stub
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getBaseMetricFromURL(@DefaultValue("http://127.0.0.1") @QueryParam("url") URL urlInput,
	    @DefaultValue("true") @HeaderParam("save") boolean db, @HeaderParam("classmetrics") boolean classMetrics)
	    throws ParserConfigurationException, ImpossibleModificationException, MalformedURLException {

	if (urlInput.sameFile(new URL("http://127.0.0.1")))
	    throw new WrongURIException();
	IRI url = IRI.create(urlInput);
	myLogger.debug("Get-URL: "
		+ url.toString());
	setDbSave(db);
	OntologyUtility.setTimestamp();

	OntologyMetricManagerImpl manager = new OntologyMetricManagerImpl();

	OWLOntology ontology = manager.loadOntologyFromIRI(url);
	if (ontology == null)
	    throw new WrongURIException();
	Directives xmlDirectives = new Directives().add("OntologyMetrics");
	Map<String, Object> map = manager.calculateMetrics(ontology);
	xmlDirectives.append(map2XML(map));
	if (classMetrics)
	    xmlDirectives = classMetrics2XML(manager, xmlDirectives);
	Xembler xml = new Xembler(xmlDirectives);
	String xmlString = xml.xml();
	return Response.ok(xmlString).header("saved", db).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Response getBaseMetricFromOntology(String request, @DefaultValue("true") @HeaderParam("save") boolean db,
	    @DefaultValue("false") @HeaderParam("classmetrics") boolean classMetrics)
	    throws ImpossibleModificationException {
	OntologyMetricManagerImpl manager = new OntologyMetricManagerImpl();
	myLogger.info("Get-from-Post");
	setDbSave(db);
	OWLOntology ontology;
	try {
	    ontology = manager.loadOntologyFromText(request);
	} catch (Exception e) {
	    throw new InvalidOntologyException();
	}
	if (ontology == null) {
	    throw new InvalidOntologyException();
	}
	myLogger.debug("Ontology loaded");
	OntologyUtility.setTimestamp();
	Directives xmlDirectives = new Directives().add("OntologyMetrics");

	// xmlDirectives = metrics2xml(manager.getMetrics(), xmlDirectives);
	manager.calculateMetrics(ontology);
//	if (classMetrics)
//	    xmlDirectives = classMetrics2XML(manager, xmlDirectives);
	Xembler xml = new Xembler(xmlDirectives);
	String xmlString = xml.xml();

	return Response.ok(xmlString).header("saved", db).build();
    }

    protected Directives map2XML(Map<String, Object> map) {
	Directives directives = new Directives();
	for (String key : map.keySet()) {
	    directives.add(key);
	    if (map.get(key) instanceof Map) {
		System.out.println("Recursion! :"
			+ key);
		directives.append(map2XML((Map<String, Object>) map.get(key)));
	    } else {
		directives.set(map.get(key));
		System.out.println(key
			+ ":"
			+ map.get(key));
	    }
	    directives.up();

	}
	// directives.add(map);
	return directives;
    }

    protected Directives classMetrics2XML(OntologyMetricManagerImpl manager, Directives xmlBuilder) {
	myLogger.debug("Calculate Class Metrics");
	
	xmlBuilder.add("ClassMetrics");
	Set<OWLClass> om = manager.getOntology().getClassesInSignature();
	OWLOntology ontology = manager.getOntology();
	for (OWLClass owlClass : om) {
	    xmlBuilder.add("Class");
	    xmlBuilder.attr("iri", owlClass.getIRI());
	    // xmlBuilder.(owlClass.getIRI());
	    xmlBuilder.add(classMetrics.getAllMetrics(ontology, owlClass.getIRI()))
	    xmlBuilder.up();
	}
	xmlBuilder.up();
	return xmlBuilder;
    }

//    protected String sentenceToWord(String input) {
//	StringBuilder sb = new StringBuilder();
//	boolean lastLetterIsSpace = false;
//	for (char letter : input.toCharArray()) {
//	    if(lastLetterIsSpace)
//		db.
//	    if(letter == ' ')
//		lastLetterIsSpace = true;
//	    
//	    
//	    
//	    
//	}
//return null;	
//    }

//    protected Directives metrics2xml(OntologyMetrics metricManager, Directives xmlBuilder)
//	    throws ImpossibleModificationException {
//	List<OntologyMetric> metrics = metricManager.getAllMetrics();
//	xmlBuilder.add("BaseMetrics");
//	String labelBefore = null;
//
//	for (OntologyMetric metric : metrics) {
//	    if (!(metric instanceof ClassMetrics)) {
//		try {
//		    String labelNow = OntologyUtility.CleanInvalidChars(metric.getLabel());
//		    if (labelBefore == null) {
//			labelBefore = labelNow;
//			xmlBuilder.add(labelNow);
//		    } else if (!labelBefore.contentEquals(labelNow)) {
//			xmlBuilder.up();
//			xmlBuilder.add(labelNow);
//			labelBefore = labelNow;
//		    }
//		    xmlBuilder.add(OntologyUtility.CleanInvalidChars(metric.toString()));
//		    xmlBuilder.set(metric.getValue().toString());
//		    xmlBuilder.up();
//		} catch (Exception e) {
//		    e.printStackTrace();
//		}
//	    }
//	}
//	xmlBuilder.up();
//	return xmlBuilder;
//    }

    protected void setDbSave(boolean db) {
	if (!db) {
	    OntologyUtility.setDBSave(false);
	    myLogger.info("store_aggreement: off");
	} else {
	    OntologyUtility.setDBSave(true);
	    myLogger.info("store_aggreement: on - Implicit Handling");
	}
    }
}
