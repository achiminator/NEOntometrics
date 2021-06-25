package de.edu.rostock.ontologymetrics.owlapi.ontology;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.UnloadableImportException;

import de.edu.rostock.ontologymetrics.owlapi.db.DBHandler;

public class OntologyMetricManagerImpl  {

    // ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
    private Logger myLogger = Logger.getLogger(this.getClass());
    protected OWLOntologyManager manager;
    
    private OWLOntology ontology;
    DBHandler db;
    

    public OntologyMetricManagerImpl() {
	manager = OWLManager.createOWLOntologyManager();
	myLogger.debug("manager created: "
		+ manager.toString());
	
	OntologyUtility.initMessages(); // reset Messages
	OntologyUtility.initImports(); // reset Import-Count
	db = new DBHandler();
    }

    public OWLOntology loadOntologyFromIRI(IRI pIRI){
	myLogger.info("loadOntologyFromIRI - Start");
	myLogger.debug("pIRI: "
		+ pIRI.toString());

	
	Boolean Loadfailure = true;

	// for escape imports with failures
	OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();

	if (pIRI != null) {
	    // for re-execute without unloadable imports
	    IRIDocumentSource source = new IRIDocumentSource(pIRI);

	    // as long as import problems there
	    while (Loadfailure) {
		try {
		    ontology = manager.loadOntologyFromOntologyDocument(source, config);
		    //OntologyMetricsImpl ontoMetric = new OntologyMetricsImpl(ontology);
		    // no more failures
		    Loadfailure = false;
		} catch (UnloadableImportException e) {
		    // more failures there
		    Loadfailure = true;
		    myLogger.trace("Importfehler loadOntologyFromIRI bei: "
			    + e.getImportsDeclaration().toString());
		    // Write warnings to result
		    OntologyUtility.setMessages(e.toString(), "W");
		    

		    // adjust the configuration
		    config.addIgnoredImport(e.getImportsDeclaration().getIRI());
		    throw e;
		} catch (UnparsableOntologyException e) {
		    System.out.println("UnparsableOntologyException: "
			    + e.toString());
		    // Write errors to result
		    OntologyUtility.setMessages(
			    "org.semanticweb.owl.io.UnparsableOntologyException: Could not parse ontology.  Either a suitable parser could not be found, or parsing failed. Please review the ontology passed.",
			    "E");
		    Loadfailure = false;
		} catch (Exception e) {
		    System.out.println("General error: "
			    + e.toString());
		    // Write errors to result
		    OntologyUtility.setMessages("General error: "
			    + e.toString(), "E");
		    Loadfailure = false;
		}
	    }
	    if (ontology != null) {

		myLogger.info("Ontology Loaded...");
		myLogger.info("Document IRI: "
			+ pIRI);
		myLogger.info("Ontology : "
			+ ontology.getOntologyID());
		myLogger.info("Format      : "
			+ manager.getOntologyFormat(ontology));

		URL url = null;
		try {
		    url = new URL(pIRI.toString());

		    String tDir = System.getProperty("java.io.tmpdir");
		    String path = tDir
			    + "URL-"
			    + OntologyUtility.getExtension(url.toString());
		    myLogger.info("URL-File Download Path: "
			    + path);
		    File file = new File(path);
		    myLogger.info("File path: "
			    + file.getAbsolutePath());
		    file.deleteOnExit();
		    FileUtils.copyURLToFile(url, file);

		    // write ontology to database
		    db.writeFile2Database(file, ontology);

		    file.delete();
		} catch (MalformedURLException e1) {
		    myLogger.debug("MalformedURLException: "
			    + e1.toString());
		    e1.printStackTrace();
		} catch (IOException e) {
		    myLogger.debug("IOException: "
			    + e.toString());
		    e.printStackTrace();
		}
	    }
	}

	myLogger.info("loadOntologyFromIRI - successfully done");
	return ontology;
    }
    /***
     * Takes a File and parses it into an Ontology. Also calculate
     * 
     * @param file an Ontological Representation
     * @return {@link OWLOntology}
     * @throws OWLOntologyCreationException
     */
    public OWLOntology loadOntologyFromFile(File file) throws OWLOntologyCreationException {
	myLogger.info("loadOntologyFromFile - Start");
	myLogger.debug("file: "
		+ file.getName());

	OWLOntology ontology = null;
	Boolean Loadfailure = true;

	// for escape imports with failures
	OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();

	if (file != null) {
	    // re-execute without unloadable imports
	    FileDocumentSource source = new FileDocumentSource(file);

	    // as long as import problems there
	    while (Loadfailure) {
		try {
		    ontology = manager.loadOntologyFromOntologyDocument(source, config);
		    // no more failures
		    Loadfailure = false;
		} catch (UnloadableImportException e) {
		    // more failures there
		    Loadfailure = true;
		    myLogger.error("Importfehler loadOntologyFromOntologyDocument bei: "
			    + e.getImportsDeclaration().toString());
		    System.out.println("Fehler: "
			    + e.toString());

		    // Write warnings to result
		    OntologyUtility.setMessages(e.toString(), "W");

		    // adjust the configuration
		    config.addIgnoredImport(e.getImportsDeclaration().getIRI());
		} catch (UnparsableOntologyException e) {
		    System.out.println("ParseEx: "
			    + e.toString());
		    // Write errors to result
		    OntologyUtility.setMessages(
			    "org.semanticweb.owl.io.UnparsableOntologyException: Could not parse ontology. Either a suitable parser could not be found, or parsing failed. Please review the ontology passed.",
			    "E");
		    Loadfailure = false;

		} catch (Exception e) {
		    System.out.println("General error: "
			    + e.toString());
		    // Write errors to result
		    OntologyUtility.setMessages("General error: "
			    + e.toString(), "E");
		    Loadfailure = false;
		}
	    }
	    if (ontology != null) {

		myLogger.info("Ontology Loaded...");
		myLogger.info("Ontology : "
			+ ontology.getOntologyID());
		myLogger.info("Format      : "
			+ manager.getOntologyFormat(ontology));
	    }
	}
	myLogger.info("loadOntologyFromFile - successfully done");
	return ontology;
    }

    /***
     * Creates a tmp file out of a String representation of an ontology utilizing
     * the {@link loadOntologyFromFile} function. Also responsible for saving it
     * into the database if allowed by End-User
     * 
     * @param text Textual representation of the Ontology
     * @return {@link OWLOntology}
     * @throws {@link OWLOntologyCreationException, IOException}
     */
    public OWLOntology loadOntologyFromText(String text) throws IOException, OWLOntologyCreationException {
	myLogger.debug("loadOntologyFromText - Start");
	/**
	 * file need a specified path /tmp on server! for local development need path
	 * c:/tmp
	 **/
	OWLOntology lontology = null;
	File file = new File(File.separator
		+ "tmp/tmp.rdf");
	FileWriter writer = new FileWriter(file, false);

	writer.write(text);
	writer.flush();
	writer.close();
	file.createNewFile();

	Date dNow = new Date();
	SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss-SS");

	String i = "tmp/"
		+ ft.format(dNow)
		+ ".rdf"; // writing it into path
			  // and filename

	myLogger.debug("Path: "
		+ i);

	File file2 = new File(File.separator + i); // i = pathname
	FileWriter writer2 = new FileWriter(file2, false);

	writer2.write(text);
	writer2.flush();
	writer2.close();
	file2.createNewFile();

	lontology = loadOntologyFromFile(file);

	// write ontology to database
	db.writeFile2Database(file, lontology);

	myLogger.debug("loadOntologyFromText successfully done");

	return lontology;
    }

    public Map<String, Object> calculateMetrics(OWLOntology ontology) {
	OntologyMetricsImpl ontoMetric = new OntologyMetricsImpl(ontology);
	return ontoMetric.getAllMetrics();
	
    }
    
    public OWLOntology getOntology() {
	return ontology;
    }

    /***
     * 
     * @return Namespace IRI of Ontology
     */
//    public String getNamespace() {
//	myLogger.debug("metrics.getIRI: "
//		+ metrics.getIRI());
//
//	if (metrics.getIRI() != null) {
//	    myLogger.debug("metrics.getIRI.getNamespace: "
//		    + metrics.getIRI().getNamespace());
//	    return metrics.getIRI().getNamespace();
//	} else {
//	    return null;
//	}
//    }
}
