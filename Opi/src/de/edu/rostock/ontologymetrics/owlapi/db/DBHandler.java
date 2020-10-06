package de.edu.rostock.ontologymetrics.owlapi.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class DBHandler {
    private Connection con = null; // for DB-Connection
    private Logger myLogger = Logger.getLogger(this.getClass());

    /***
     * Initiates Connection using {@link initDBConnection} if DBSave attribute in
     * {@link OntologyUtility} is true. The real insertion-SQL is prepared and
     * carried out in {@link handleDB}
     * 
     * @see OntologyUtility
     * @param file     File containing the Ontology
     * @param ontology {@link OWLOntology} Object
     */
    public void writeFile2Database(File file, OWLOntology ontology) {
	// write to MySQL-Database, if user agree
	if (OntologyUtility.isDBSave()) {
	    con = initDBConnection();
	    if (con != null && ontology != null && file != null) {
		handleDB(file, ontology);
	    } else {
		myLogger.debug("Keine Daten in DB geschrieben (Fehler mit Con)");
	    }
	} else {
	    myLogger.debug("Keine Daten in DB geschrieben (Option explizit abgewählt)");
	}
    }

    /**
     * Carries out the first insertion of Data in the Database. This includes the
     * description (Meta-Data about Ontology), file-type, the file itself (Using the
     * Database as a File-Storage space and the Timestamp (inserted in the
     * XML-Database as <i>XML_IDENT</i>
     * 
     * @param file      file containing the ontology
     * @param lontology {@link OWLOntology} representation
     */
    private void handleDB(File file, OWLOntology lontology) { // handle
							      // the
	// statements
	myLogger.debug("handleDB - Start");
	myLogger.debug("file: "
		+ file.getName());

	try {
	    FileInputStream fis = new FileInputStream(file);

	    String mySQLStmt = "INSERT INTO ontologies (description, file_type, file, XML_ident) VALUES(?,?,?,?);";
	    String file_type = OntologyUtility.getExtension(file.getName()).toUpperCase();
	    String descr = "Ori-File: "
		    + OntologyUtility.getGlobFile()
		    + " Size: "
		    + file.length();

	    myLogger.debug("getOntoData: "
		    + OntologyUtility.getOntoData().size());

	    // fill description with information from the ontology
	    for (String value : OntologyUtility.getOntoData()) {
		descr += "; "
			+ value;
	    }

	    myLogger.debug("Description: "
		    + descr);
	    myLogger.debug("File-Type: "
		    + file_type);

	    if (!con.isClosed()) {
		PreparedStatement pstmt = con.prepareStatement(mySQLStmt);
		pstmt.setString(1, descr);
		pstmt.setString(2, file_type);
		pstmt.setBlob(3, fis);
		pstmt.setString(4, OntologyUtility.getTimestamp());

		int r = pstmt.executeUpdate();
		myLogger.debug("SQL-Befehl zur Speicherung der übertragenden Datei: "
			+ pstmt);
		myLogger.debug(r
			+ " Zeilen angelegt.");
	    } else {
		myLogger.debug("Non open connection");
	    }
	    closeDBConnection(con);

	    myLogger.debug("handleDB - successfully done");
	} catch (FileNotFoundException e) {
	    myLogger.error("FileNotFound - failure: "
		    + e.toString());
	    // Write errors to result
	    OntologyUtility.setMessages(e.toString(), "E");
	} catch (SQLException e) {
	    myLogger.error("SQL - failure: "
		    + e.getErrorCode()
		    + ": "
		    + e.toString());
	    // Write errors to result
	} catch (Exception e) {
	    myLogger.error("Error - failure: "
		    + e.toString());
	    // others ignored
	}
    }

    public void closeDBConnection(Connection con) { // connect to the
	// database
	try {
	    if (!con.isClosed())
		con.close();
	} catch (SQLException e) {
	    myLogger.error("Connection close - failure: "
		    + e.toString());
	}
    }

    public void writeXMLResult2DB(File result, String indentifier) {
	String mySQLStmt = "UPDATE ontologies SET XML_result = ? where XML_ident = ?;";

	if (result == null || indentifier == null)
	    return;

	Connection myCon = initDBConnection();

	try {
	    FileInputStream fis = new FileInputStream(result);

	    if (!myCon.isClosed()) {
		PreparedStatement pstmt = myCon.prepareStatement(mySQLStmt);
		pstmt.setBlob(1, fis);
		pstmt.setString(2, indentifier);

		int r = pstmt.executeUpdate();
		myLogger.debug("SQL-Befehl zur Speicherung des übertragenen Results: "
			+ pstmt);
		myLogger.debug(r
			+ " Zeilen geändert.");
	    }
	} catch (SQLException e) {
	    myLogger.error("SQL - failure: "
		    + e.getErrorCode()
		    + ": "
		    + e.toString());
	} catch (FileNotFoundException e) {
	    myLogger.error("FileNotFound - failure: "
		    + e.toString());
	}
	closeDBConnection(myCon);
    }

    public Connection initDBConnection() { // connect to the database
	Connection con = null;

	try {
	    Class.forName("com.mysql.jdbc.Driver");
	    // Read Password from environment Variable "db_password
	    String dbPw = System.getenv("db_password");

	    // localhost=root server
	    con = DriverManager.getConnection("jdbc:mysql://mysql:3306/ontometrics", "user", dbPw);

	    myLogger.debug("Verbindungssatus: "
		    + con.getCatalog());

	} catch (ClassNotFoundException e) {
	    myLogger.error("JDBC-Driver Class - failure: "
		    + e.toString());
	} catch (SQLException e) {
	    myLogger.error("Connection - failure: "
		    + e.toString());
	}
	return con;
    }
}
