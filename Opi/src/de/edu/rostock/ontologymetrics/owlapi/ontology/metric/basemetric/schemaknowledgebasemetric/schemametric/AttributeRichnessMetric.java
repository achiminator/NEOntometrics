package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.schemametric;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

/**
 * Return the average number of attributes per class.<br>
 * In the OWL 2 Structural Specification, an attribute is declared as a
 * functional data property.
 */

//TEST OK
public class AttributeRichnessMetric extends SchemaMetric {

	Logger myLogger = Logger.getLogger(this.getClass());

	public AttributeRichnessMetric(OWLOntology pOntology) {
		super(pOntology);
	}

	@Override
	public Double getValue() {
		double ret = 0;
		float classes = parserI.getNoClasses();
		int datapropertycount = ontology.getDataPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();

		if (classes == 0) {
			ret = 0f;
		} else {
			ret = (double) datapropertycount / (double) classes;
			myLogger.debug("datapropertycount/classes=" + datapropertycount + "/" + classes);
		}

		return OntologyUtility.roundByGlobNK(ret);
	}

	@Override
	public String toString() {
		return "Attribute richness";
	}

}
