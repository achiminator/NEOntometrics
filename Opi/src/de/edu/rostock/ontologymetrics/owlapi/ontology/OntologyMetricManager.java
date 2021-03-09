package de.edu.rostock.ontologymetrics.owlapi.ontology;

import java.io.File;
import java.io.IOException;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public interface OntologyMetricManager {

    public OWLOntology loadOntologyFromIRI(IRI pIRI);

    /***
     * Takes a File and parses it into an Ontology. Also calculate
     * 
     * @param file an Ontological Representation
     * @return {@link OWLOntology}
     * @throws OWLOntologyCreationException
     */
    public OWLOntology loadOntologyFromFile(File file) throws OWLOntologyCreationException;

    /***
     * Creates a tmp file out of a String representation of an ontology utilizing
     * the {@link loadOntologyFromFile} function. Also responsible for saving it
     * into the database if allowed by End-User
     * 
     * @param text Textual representation of the Ontology
     * @return {@link OWLOntology}
     * @throws {@link OWLOntologyCreationException, IOException}
     */

    public OWLOntology loadOntologyFromText(String text) throws IOException, OWLOntologyCreationException;

    public OWLOntology getOntology();
/***
 * Returns the instantiated {@link OntologyMetrics}
 * 
 * @return {@link OntologyMetrics}
 */
    public OntologyMetrics getMetrics();
/***
 * 
 * @return Namespace IRI of Ontology
 */
    public String getNamespace();

}
