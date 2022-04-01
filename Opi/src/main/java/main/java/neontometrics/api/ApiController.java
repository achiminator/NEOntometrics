package main.java.neontometrics.api;

import java.io.ByteArrayInputStream;
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


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLDocumentFormatImpl;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration.MissingOntologyHeaderStrategy;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

import main.java.neontometrics.api.error.InvalidOntologyException;
import main.java.neontometrics.api.error.WrongURIException;
import main.java.neontometrics.calc.handler.OntologyMetricsImpl;
import main.java.neontometrics.calc.handler.OntologyUtility;

@Path("")
public class ApiController {

    public ApiController() {
	// TODO Auto-generated constructor stub
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getBaseMetricFromURL(@DefaultValue("http://127.0.0.1") @QueryParam("url") URL urlInput,
	     @DefaultValue("false") @HeaderParam("classmetrics") boolean classMetrics, @DefaultValue("false") @HeaderParam("reasoner") boolean reasoner)
	    throws Exception {
	if (urlInput.sameFile(new URL("http://127.0.0.1")))
	    throw new WrongURIException();
	IRI url = IRI.create(urlInput);
	OntologyUtility.setTimestamp();
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	OWLOntology ontology = manager.loadOntologyFromOntologyDocument(url);
	if (ontology == null)
	    throw new WrongURIException();
	return calculateMetrics(classMetrics,reasoner, ontology);
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Response getBaseMetricFromOntology(String request, 
	    @DefaultValue("false") @HeaderParam("classmetrics") boolean classMetrics, @DefaultValue("false") @HeaderParam("reasoner") boolean reasoner) throws Exception {
	//OntologyMetricManagerImpl manager = new OntologyMetricManagerImpl();
	OWLOntologyLoaderConfiguration loaderConfig = new OWLOntologyLoaderConfiguration();
	loaderConfig = loaderConfig.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
	OWLOntologyDocumentSource documentSource = new StringDocumentSource(request);
	
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	OWLOntology ontology;
	try {
	    ontology = manager.loadOntologyFromOntologyDocument(documentSource, loaderConfig);
	    
	} catch (Exception e) {
	    throw new InvalidOntologyException();
	}
	if (ontology == null) {
	    throw new InvalidOntologyException();
	}
	OntologyUtility.setTimestamp();

	return calculateMetrics(classMetrics, reasoner, ontology);
    }

    @SuppressWarnings("unchecked")
    protected Directives map2XML(Map<String, Object> map) {
	Directives directives = new Directives();
	for (String key : map.keySet()) {
	    directives.add(key);
	    if (map.get(key) instanceof Map) {
		
		directives.append(map2XML((Map<String, Object>) map.get(key)));
	    } else {
		directives.set(map.get(key));
		
	    }
	    directives.push();
	    directives.up();

	}
	return directives;
    }

    protected Directives classMetrics2XML(List<Map<String, Object>> classMetrics, Directives xmlBuilder) {
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

    protected Response calculateMetrics(boolean classMetrics, boolean reasoner, OWLOntology ontology)
	    throws Exception, ImpossibleModificationException {

	OntologyMetricsImpl ontoMetricsEnginge = new OntologyMetricsImpl(ontology);
	Directives xmlDirectives = new Directives().add("OntologyMetrics");
	Map<String, Object> map = ontoMetricsEnginge.getAllMetrics(reasoner);
	xmlDirectives.append(map2XML(map));


	Xembler xml = new Xembler(xmlDirectives);
	String xmlString = xml.xml();
	return Response.ok(xmlString).header("saved", "False").build();
    }
}
