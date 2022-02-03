package de.edu.rostock.ontologymetrics.owlapi.ontology;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;

public class OntologyUtility {

    private static Logger myLogger = Logger.getLogger("OntologyUtility");

    private static double GlobNK = 6.0;
    private static String GlobTimestamp = null;
    private static int CountImports = 0;
    private static String messages = "";
    private static String errors = "";
    private static String globFile = "";
    private static boolean DBSave = false;
    private static boolean classMetrics = false;
    private static List<String> OntoData = new ArrayList<String>();

    /***
     * Prints an array containing Meta-Data about Ontology.
     * 
     * @see {@link addOntoData}
     * @return Array containing Meta-Data about Ontology.
     */
    public static List<String> getOntoData() {
	return OntoData;
    }

    /***
     * Adds Data into an Array for storing Meta-Data of the ontology. Is the
     * foundation for the description column in the database.
     * 
     * @param value String Value Containing Meta-Data about Ontology
     */
    public static void addOntoData(String value) {
	if (!value.contains("null")) {
	    OntoData.add(value);
	}
    }

    public static boolean isDBSave() {
	return DBSave;
    }

    public static void setDBSave(boolean dBSave) {
	DBSave = dBSave;
    }

    /**
     * Returns the usage of Imports
     * 
     * @return Count of used Imports
     */
    public static int getCountImports() {
	return CountImports;
    }

    /**
     * Increments the count of Imports
     */
    private static void incrementImports() {
	CountImports++;
    }

    /**
     * Initialization of the count of Imports
     */
    public static void initImports() {
	CountImports = 0;
    }

    /**
     * Initialization of output-messages (Warnings and errors for the result)
     */
    public static void initMessages() {
	OntologyUtility.messages = "";
	OntologyUtility.errors = "";
    }

    /**
     * Returns warnings or errors of important content failures
     * 
     * @param type - 'E' error or other
     * @return Warning- or error-messages for the result
     */
    public static String getMessages(String type) {
	myLogger.debug("getMessages: "
		+ messages);
	if (type == "E")
	    return errors;
	else
	    return messages;
    }

    /**
     * Set the content failure text
     * 
     * @param messages - content failure
     * @param type     - 'E' error or other
     */
    public static void setMessages(String ProbTxt, String type) {
	myLogger.debug("type: "
		+ type);

	if (ProbTxt == null) {
	    return;
	} else {
	    if (ProbTxt.length() > 255)
		ProbTxt.substring(0, 255);

	    myLogger.debug("setMessages: "
		    + ProbTxt);

	    if (type == "E") {
		if (!OntologyUtility.errors.isEmpty())
		    OntologyUtility.errors += ";\n\n"
			    + ProbTxt;
		else
		    OntologyUtility.errors = ProbTxt;
	    } else {
		if (!OntologyUtility.messages.isEmpty())
		    OntologyUtility.messages += ";\n\n"
			    + ProbTxt;
		else
		    OntologyUtility.messages = ProbTxt;
	    }
	}
    }

    /**
     * Returns the path or the filename of the user-imported file
     * 
     * @return path or file name of the imported file
     */
    public static String getGlobFile() {

	/*
	 * According to the specifications of HTML5, a file upload control should not
	 * reveal the real local path to the file you have selected, if you manipulate
	 * its value string with JavaScript. Instead, the string that is returned by the
	 * script, which handles the file information is C:\fakepath. This requirement
	 * is already implemented in Internet Explorer 8 - the real path to the file
	 * will be shown only if the page that contains the control is added to the
	 * trusted sites collection of the browser.
	 */
	return globFile.replace("C:\\fakepath\\", "");
    }

    /**
     * Set the path or file name of the user-imported file
     * 
     * @param globFile is the path or file name of the imported file
     */
    public static void setGlobFile(String globFile) {
	if (globFile == null)
	    return;

	OntologyUtility.globFile = globFile;
    }

    public static boolean isClassMetrics() {
	return classMetrics;
    }

    public static void setClassMetrics(boolean classMetrics) {
	OntologyUtility.classMetrics = classMetrics;
    }

    public static void setTimestamp() {
	GlobTimestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }

    public static String getTimestamp() {
	return GlobTimestamp;
    }

    /**
     * Return an <code>OWLClass</code> corresponding to the given IRI.
     * 
     * @param iri
     * @return If the given IRI is not available or the object is no
     *         <code>OWLClass</code>, this method return <code>null</code>.
     */
    public static OWLClass getClass(OWLOntology ontology, IRI iri) {
	OWLClass clazz = null;

	if (ontology != null) {
	    for (OWLClass cls : ontology.getClassesInSignature(OntologyUtility.ImportClosures(true))) {
		if (cls.getIRI() != null && cls.getIRI().equals(iri)) {
		    clazz = cls;
		    break;
		}
	    }
	}

	return clazz;
    }

    public static void printClassHierarchy(OWLOntology ontology) {
	if (ontology != null) {
	    printClassHierarchyRecursive(ontology, ontology.getClassesInSignature(OntologyUtility.ImportClosures(true)),
		    0);
	}
    }

    public static void printClassHierarchyRecursive(OWLOntology ontology, Set<OWLClass> subClasses, int level) {
	if (subClasses != null) {

	    for (OWLClass subClass : subClasses) {
		for (int i = 0; i < level; i++) {
		    System.out.print("\t");
		}
		// MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
		if (!EntitySearcher.getSubClasses(subClass, ontology).isEmpty()) {
		    printClassHierarchyRecursive(ontology, subClass.getClassesInSignature(), level + 1);
		}
		/*
		 * //MLIC Umstellung OWL API von 4.2.3 auf 5.0.1 (EntitySearcher arbeitet mit
		 * Streams) if (EntitySearcher.getSubClasses(subClass,ontology).count()>0) {
		 * printClassHierarchyRecursive(ontology, subClass.getClassesInSignature(),
		 * level + 1); }
		 */
	    }
	}
    }

    /**
     * Function to capsules the used imports in Statements
     * 
     * @return Enum for Imports (INCLUDED or EXCLUDED)
     */
    public static Imports ImportClosures() {
	OntologyUtility.incrementImports();
	return Imports.INCLUDED;
    }

    public static Imports ImportClosures(boolean imp) {
	OntologyUtility.incrementImports();
	return imp ? Imports.INCLUDED : Imports.EXCLUDED;
    }

    /**
     * Function to get the last file-extension of a path
     * 
     * @param file filename or path
     * @return file-extension
     */
    public static String getExtension(String file) {
	if (file != null) {
	    if (file.lastIndexOf(".") > 0)
		return file.substring(file.lastIndexOf(".") + 1);
	    else
		return file.toUpperCase();
	}
	return "";
    }

    /**
     * Function to get the user name of the used server
     * 
     * @return user name
     */
    public static String getUserName() {
	return System.getProperty("user.name"); // platform independent
    }

    /**
     * Function to get the name of the used server (host)
     * 
     * @return host name
     */
    public static String getHostName() {
	try {
	    return InetAddress.getLocalHost().getHostName();
	} catch (UnknownHostException e) {
	    e.printStackTrace();
	    return "unknown";
	}
    }

    public static double roundByGlobNK(double value) {
	double clacValue = 1 * Math.pow(10.0, GlobNK);
	return Math.round(clacValue * value) / clacValue;
    }

    public static double roundByNK(double value, int stellen) {
	double clacValue = 1 * Math.pow(10.0, (double) stellen);
	return Math.round(clacValue * value) / clacValue;
    }

    /**
     * Return a list of namespaces defined by the classes included in the given
     * ontology.
     * 
     * @param pOntology
     * @return
     */
    public static List<String> getNamespaces(OWLOntology pOntology) {
	List<String> result = new ArrayList<String>();

	Set<OWLClass> classes = pOntology.getClassesInSignature(OntologyUtility.ImportClosures(true));
	for (OWLClass cls : classes) {
	    if (cls.asOWLClass().isClassExpressionLiteral()
		    && !cls.asOWLClass().isOWLThing()) { /* only named classes without Thing */
		String value = cls.getIRI().getNamespace() + cls.getIRI().getShortForm();
		result.add(value);
	    }
	}

	// Sort of the classes
	java.util.Collections.sort(result);
	return result;
    }

    /**
     * A little helper class that extracts full Classes out of a Collection of
     * OWLClassExpressions. This is helpful if one wants to consider only Real
     * Classes in the subclass tree, not statements like "isEncded exactly 1
     * owl:thing"
     * 
     * @param classExpressions A Collection of {@link OWLClassExpression}
     * @return the set of {@link OWLClass}.
     */
    public static Collection<OWLClass> classExpr2classes(Collection<OWLClassExpression> classExpressions) {
	Set<OWLClass> classes = new TreeSet<OWLClass>();
	for (OWLClassExpression classExpression : classExpressions) {
	    if(classExpression instanceof OWLClass)
		classes.addAll(classExpression.getClassesInSignature());
	}
	return classes;
    }
    /**
     * Extracts object Properties out of a Collection of {@link OWLClassExpression}
     * @param classExpressions A collection of {@link OWLClassExpression}
     * @return set of {@link OWLObjectProperty}
     */
    public static Collection<OWLObjectProperty> classExpr2ObjectProperties(Collection<OWLClassExpression> classExpressions){
	Set<OWLObjectProperty> oProperties = new TreeSet<OWLObjectProperty>();
	    for (OWLClassExpression owlClassExpression : classExpressions) {
		oProperties.addAll(owlClassExpression.getObjectPropertiesInSignature());
	    }
	    return oProperties;
    }

    /**
     * Prepares a given string for XML-represenation Delete all invalid signs
     * 
     * @param text input text for parsing
     * @return XML-valid representation
     */
    public static String CleanInvalidChars(String text) {

	String cleanText = text;
	cleanText = cleanText.replace("#", "");
	cleanText = cleanText.replace(".", "");
	cleanText = cleanText.replace("/", "");
	cleanText = cleanText.replace(":", "");
	cleanText = cleanText.replace("-", "");
	cleanText = cleanText.replace(" ", "");

	Pattern INVALID_XML_CHARS = Pattern
		.compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\uD800\uDC00-\uDBFF\uDFFF]");
	return INVALID_XML_CHARS.matcher(cleanText).replaceAll("");
    }

}
