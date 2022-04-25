package main.java.neontometrics.api;

import java.net.URL;
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
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    @Produces(MediaType.APPLICATION_JSON)
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
    @Produces(MediaType.APPLICATION_JSON)
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

//    @SuppressWarnings("unchecked")
//    protected Directives map2XML(Map<String, Object> map) {
//	Directives directives = new Directives();
//	for (String key : map.keySet()) {
//	    directives.add(key);
//	    if (map.get(key) instanceof Map) {
//		
//		directives.append(map2XML((Map<String, Object>) map.get(key)));
//	    } else {
//		directives.set(map.get(key));
//		
//	    }
//	    directives.push();
//	    directives.up();
//
//	}
//	return directives;
//    }


    protected Response calculateMetrics(boolean classMetrics, boolean reasoner, OWLOntology ontology)
	    throws Exception {

	OntologyMetricsImpl ontoMetricsEnginge = new OntologyMetricsImpl(ontology);
	Map<String, Object> map = ontoMetricsEnginge.getAllMetrics(reasoner);
	ObjectMapper mapper = new ObjectMapper();
	String reponse = mapper.writeValueAsString(map);
	
	return Response.ok(reponse).header("saved", "False").build();
    }
}
