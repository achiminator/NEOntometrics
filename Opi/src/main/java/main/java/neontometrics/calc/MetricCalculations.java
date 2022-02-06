package main.java.neontometrics.calc;

import java.util.LinkedHashMap;


import org.semanticweb.owlapi.model.OWLOntology;

public class MetricCalculations {

    protected boolean imports;
    protected OWLOntology ontology;
    protected LinkedHashMap<String, Object> returnObject;

    public MetricCalculations(OWLOntology ontology, boolean imports) {
	
	this.ontology = ontology;
	this.imports = imports;
	this.returnObject = new LinkedHashMap<String, Object>();
    }

    public LinkedHashMap<String, Object> getReturnObject() {
        return returnObject;
    }

}