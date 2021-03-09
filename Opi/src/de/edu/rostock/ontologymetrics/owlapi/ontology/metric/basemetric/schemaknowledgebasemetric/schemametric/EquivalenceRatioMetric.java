package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.schemametric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountTotalClassesMetric;

public class EquivalenceRatioMetric extends SchemaMetric {
	
    public EquivalenceRatioMetric(OWLOntology pOntology) {
	super(pOntology);
    }
	
	public Double getValue() {
		double classes = new CountTotalClassesMetric(ontology).getValue();
		// MLIC: changed getAxiomCount with boolean is deprecated
		// float sameClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES, true);
		double sameClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES, OntologyUtility.ImportClosures(true));
		
		if (classes==0) {
			return 0.0;
		} else {
		return OntologyUtility.roundByGlobNK(sameClasses/classes);
	}
	}
	
    public String toString() {
	return "Equivalence ratio";
    }
}
