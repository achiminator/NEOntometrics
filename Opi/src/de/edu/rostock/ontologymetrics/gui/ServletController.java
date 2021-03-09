package de.edu.rostock.ontologymetrics.gui;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.metrics.ImportClosureSize;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyMetricManager;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyMetricManagerImpl;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

/**
 * Servlet implementation class Servlet
 */
@WebServlet("/Servlet")
public class ServletController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    final Logger myLogger = Logger.getLogger(this.getClass());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletController() {
	super();
    }

    // MLIC new
    public void init(ServletConfig config) throws ServletException {

	myLogger.debug("Init-Param from jsp:");
	for (Enumeration<String> e = config.getInitParameterNames(); e.hasMoreElements();)
	    myLogger.debug(e.nextElement());

	System.out.println("Log4JInitServlet is initializing log4j");
	String log4jLocation = config.getInitParameter("log4j-properties-location");

	System.out.println("log4j-properties-location: "
		+ log4jLocation);

	ServletContext sc = config.getServletContext();

	if (log4jLocation == null) {
	    System.err.println(
		    "*** No log4j-properties-location init param, so initializing log4j with BasicConfigurator");
	    BasicConfigurator.configure();
	    System.out.println("Initializing log4j withOUT: PropFile complete");
	} else {
	    String webAppPath = sc.getRealPath("/");
	    System.out.println("webAppPath: "
		    + webAppPath);
	    String log4jProp = webAppPath + log4jLocation;
	    System.out.println("log4jLocation: "
		    + log4jProp);
	    File PropFile = new File(log4jProp);
	    if (PropFile.exists()) {
		System.out.println("Initializing log4j with: "
			+ log4jProp);
		PropertyConfigurator.configure(log4jProp);
		System.out.println("Initializing log4j with: "
			+ log4jProp
			+ " complete");
	    } else {
		System.err.println("*** "
			+ log4jProp
			+ " file not found, so initializing log4j with BasicConfigurator");
		BasicConfigurator.configure();
		myLogger.debug("Initializing log4j withOUT: PropFile complete");
	    }
	}
	super.init(config);
	System.out.println("Init succeeded");
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	// not used
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	myLogger.debug("doPost Start");
	myLogger.info("User: "
		+ OntologyUtility.getUserName());
	myLogger.info("Host: "
		+ OntologyUtility.getHostName());

	// jsp-sources
	myLogger.debug("Param from jsp:");
	for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();)
	    myLogger.debug(e.nextElement());

	myLogger.debug("Attributes from jsp:");
	for (Enumeration<String> e = request.getAttributeNames(); e.hasMoreElements();)
	    myLogger.debug(e.nextElement());

	String text = request.getParameter("text");
	String iriString = request.getParameter("classmetric");
	OntologyUtility.setGlobFile(request.getParameter("path"));

	// dbsave
	String dbsave = request.getParameter("store_aggreement");
	myLogger.info("store_aggreement(input): "
		+ dbsave);
	dbsave = (dbsave == null ? "off" : "on");
	myLogger.info("store_aggreement(processed):"
		+ dbsave);

	// check the permission to write data into the database
	if (dbsave == "off") {
	    myLogger.info("Not accepted to write file in DB");
	    OntologyUtility.setDBSave(false);
	} else {
	    myLogger.info("Allowed to write file in DB");
	    OntologyUtility.setDBSave(true);
	}

	// build XMLIdentifier to save the jsp-result
	OntologyUtility.setTimestamp();

	String classMetrics = request.getParameter("class");
	myLogger.info("class(input): "
		+ classMetrics);
	classMetrics = (classMetrics != null ? "on" : "off");
	myLogger.info("class(processed):"
		+ classMetrics);

	if (classMetrics == "off") {
	    myLogger.info("No classMetric-calculation selected");
	    OntologyUtility.setClassMetrics(false);
	}

	else {
	    myLogger.info("classMetric-calculation selected");
	    OntologyUtility.setClassMetrics(true);
	}

	Boolean isURL = true;

	myLogger.debug("iriString: "
		+ iriString);
	myLogger.debug("path:"
		+ request.getParameter("path"));

	// create ontology metric manager
	OntologyMetricManager manager = new OntologyMetricManagerImpl();
	OWLOntology ontology = null;

	// validate source
	IRI iri = null;

	if (text != null) {
	    // try to create an ontology from url source
	    if (text.startsWith("http") || text.startsWith("www")) {
		try {
		    // requires http to load successfully the ontology
		    if (text.startsWith("www"))
			text = "http://"
				+ text;

		    iri = IRI.create(text);
		    ontology = manager.loadOntologyFromIRI(iri);
		} catch (Exception e) {
		    // Write errors to result
		    OntologyUtility.setMessages(e.getMessage(), "E");
		    myLogger.error("doPost: "
			    + e.getMessage());
		}
	    } else {
		try {
		    ontology = manager.loadOntologyFromText(text);
		    isURL = false;
		} catch (UnparsableOntologyException e) {
		    // Write errors to result
		    OntologyUtility.setMessages(e.getMessage(), "E");
		    myLogger.error("doPost: "
			    + e.getMessage());
		}
//		    
		catch (OWLOntologyCreationException e) {
		    // Write errors to result
		    OntologyUtility.setMessages(e.getMessage(), "E");
		    myLogger.error("doPost: "
			    + e.getMessage());
		}
	    }
	    // @TODO: That here is just not nice. It works asynchronous, even though it is
	    // never declared that way - but just depends on the reponse time of the
	    // database server. If one wants to debug that code - it doesn't work anymore.
	    if (ontology != null && manager != null) {
		// fill ontology context data for db-description
		OntologyUtility.addOntoData("Ontology-ID: "
			+ ontology.getOntologyID().toString());
		OntologyUtility.addOntoData("Ontology-IRI: "
			+ ontology.getOntologyID().getOntologyIRI());
		OntologyUtility.addOntoData("Format: "
			+ ontology.getOWLOntologyManager().getOntologyFormat(ontology));
		OntologyUtility.addOntoData("ImportClosureSize: "
			+ (new ImportClosureSize(ontology)).getValue());

		OntologyUtility.addOntoData("IRI: "
			+ iri);
		OntologyUtility.addOntoData("Namespace: "
			+ manager.getNamespace());
		OntologyUtility.addOntoData("IRI-String: "
			+ iriString);

		// create output for view from model
		// the results will be passed back (as an attribute) to the JSP
		// view
		// (result.jsp)
		request.setAttribute("ontoid", ontology.getOntologyID().getOntologyIRI().toString());
		request.setAttribute("metrics", manager.getMetrics());
		request.setAttribute("namespaces", OntologyUtility.getNamespaces(manager.getOntology()));
		request.setAttribute("xmlident", OntologyUtility.getTimestamp());
	    }
	    // create output for view from model
	    // the results will be passed back (as an attribute) to the JSP
	    // view
	    // (result.jsp)

	    request.setAttribute("isurl", isURL);
	    request.setAttribute("classmetric", iriString);

	    // Warnings new
	    request.setAttribute("messages", OntologyUtility.getMessages("W"));
	    // Errors new
	    request.setAttribute("errors", OntologyUtility.getMessages("E"));
	    for (Enumeration<String> e = request.getAttributeNames(); e.hasMoreElements();)
		myLogger.debug(e.nextElement());
	    if (iri != null && manager != null) {
		String ret = manager.getNamespace();

		myLogger.debug("iri: "
			+ iri.toString());
		myLogger.debug("src: "
			+ ret);
		request.setAttribute("src", ret);
	    }

	    RequestDispatcher view = request.getRequestDispatcher("result.jsp");
	    view.forward(request, response);
	}
	myLogger.info("OntologyUtility.getCountImports(): "
		+ OntologyUtility.getCountImports());
	myLogger.debug("doPost succeeded");
    }
}
