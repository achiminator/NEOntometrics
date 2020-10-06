package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.schemametric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class InverseRelationsRatioMetric extends SchemaMetric {
	
	
	public InverseRelationsRatioMetric(OWLOntology pOntology) {
		super(pOntology);
	    }
		
	    public Double getValue() {
	    	// MLIC: changed functions with boolean is deprecated
	    	/*
	    	float objectpropertycount = ontology.getObjectPropertiesInSignature(true).size();
	    	float inverseobjectproperties = ontology.getAxiomCount(AxiomType.INVERSE_OBJECT_PROPERTIES, true);
	    	float inversefunctionalproperties = ontology.getAxiomCount(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, true);
	    	float functionaldatapropertycount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_DATA_PROPERTY, true);
	    	*/
	    	double objectpropertycount = ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();
	    	double inverseobjectproperties = ontology.getAxiomCount(AxiomType.INVERSE_OBJECT_PROPERTIES, OntologyUtility.ImportClosures(true));
	    	double inversefunctionalproperties = ontology.getAxiomCount(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, OntologyUtility.ImportClosures(true));
	    	double functionaldatapropertycount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_DATA_PROPERTY, OntologyUtility.ImportClosures(true));
	    	if (objectpropertycount+functionaldatapropertycount==0) {
	    		return 0.0;	    		
	    	} else {
	    	return OntologyUtility.roundByGlobNK((inverseobjectproperties+inversefunctionalproperties)/(objectpropertycount+functionaldatapropertycount));
	    	}
	    }
	
    public String toString() {
	return "Inverse relations ratio";
    }
}
