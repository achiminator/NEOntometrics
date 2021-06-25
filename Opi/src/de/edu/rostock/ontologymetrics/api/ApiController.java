package de.edu.rostock.ontologymetrics.api;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;
import de.edu.rostock.ontologymetrics.api.error.InvalidOntologyException;
import de.edu.rostock.ontologymetrics.api.error.WrongURIException;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyMetricManagerImpl;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyMetricsImpl;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

@Path("")
public class ApiController {

    final Logger myLogger = Logger.getLogger(this.getClass());

    public ApiController() {
	// TODO Auto-generated constructor stub
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getBaseMetricFromURL(@DefaultValue("http://127.0.0.1") @QueryParam("url") URL urlInput,
	     @DefaultValue("false") @HeaderParam("classmetrics") boolean classMetrics)
	    throws Exception {
	if (urlInput.sameFile(new URL("http://127.0.0.1")))
	    throw new WrongURIException();
	IRI url = IRI.create(urlInput);
	myLogger.debug("Get-URL: "
		+ url.toString());
	OntologyUtility.setTimestamp();
	OntologyMetricManagerImpl manager = new OntologyMetricManagerImpl();
	OWLOntology ontology = manager.loadOntologyFromIRI(url);
	if (ontology == null)
	    throw new WrongURIException();
	return calculateMetrics(classMetrics, manager);
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Response getBaseMetricFromOntology(String request, 
	    @DefaultValue("false") @HeaderParam("classmetrics") boolean classMetrics) throws Exception {
	OntologyMetricManagerImpl manager = new OntologyMetricManagerImpl();
	myLogger.info("Get-from-Post");
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

	return calculateMetrics(classMetrics, manager);
    }

    @SuppressWarnings("unchecked")
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
	    directives.push();
	    directives.up();

	}
	return directives;
    }

    protected Directives classMetrics2XML(List<Map<String, Object>> classMetrics, Directives xmlBuilder) {
	myLogger.debug("Calculate Class Metrics");
	xmlBuilder.pop();
	xmlBuilder.add("ClassMetrics");
	for (Map<String, Object> classMetric : classMetrics) {
	    xmlBuilder.add("Class");
	    xmlBuilder.attr("iri", classMetric.get("iri"));
	    classMetric.remove("iri");

	    xmlBuilder.add(classMetric);
	    xmlBuilder.up();
	}
	xmlBuilder.up();
	return xmlBuilder;
    }

    protected Response calculateMetrics(boolean classMetrics, OntologyMetricManagerImpl manager)
	    throws Exception, ImpossibleModificationException {

	OntologyMetricsImpl ontoMetricsEnginge = new OntologyMetricsImpl(manager.getOntology());
	Directives xmlDirectives = new Directives().add("OntologyMetrics");
	Map<String, Object> map = ontoMetricsEnginge.getAllMetrics();
	xmlDirectives.append(map2XML(map));

	if (classMetrics)
	    xmlDirectives = classMetrics2XML(ontoMetricsEnginge.getClassMetrics(), xmlDirectives);

	Xembler xml = new Xembler(xmlDirectives);
	String xmlString = xml.xml();
	return Response.ok(xmlString).header("saved", "False").build();
    }
}
