package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.schemametric;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountSubClassOfAxiomsMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountTotalClassesMetric;

/**
 * Return the average number of subclasses per class.
 */
public class SchemaInheritenceRichnessMetric extends SchemaMetric {

	Logger myLogger = Logger.getLogger(this.getClass());
	
    public SchemaInheritenceRichnessMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    /**
     * the inheritance richness of the schema (IRs) 
     * is defined as the average number of subclasses per class
     */
    public Double getValue() {
	
    double countSubClasses = new CountSubClassOfAxiomsMetric(ontology).getValue();
    double countClasses = new CountTotalClassesMetric(ontology).getValue();

	// avoid a division by zero
	if (countClasses == 0) {
	    return 0.0;
	} else {
		myLogger.debug("countSubClasses: "+countSubClasses);
		myLogger.debug("countClasses: "+countClasses);
		myLogger.debug("countSubClasses / countClasses: "+countSubClasses / countClasses);
	    double ret = countSubClasses / countClasses;
	    return OntologyUtility.roundByGlobNK(ret);
	}
    }

    @Override
    public String toString() {
	return "Inheritance richness";
    }

}
