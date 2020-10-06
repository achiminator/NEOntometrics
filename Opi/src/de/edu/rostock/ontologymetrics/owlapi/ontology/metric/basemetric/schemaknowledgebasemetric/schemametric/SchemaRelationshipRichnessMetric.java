package de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.schemametric;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountLogicalAxiomsMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.CountSubClassOfAxiomsMetric;

public class SchemaRelationshipRichnessMetric extends SchemaMetric {

    public SchemaRelationshipRichnessMetric(OWLOntology pOntology) {
	super(pOntology);
    }

    @Override
    public Double getValue() {
	// TODO logical axioms describe all relationships (arcs) in the
	// ontology?
	double inheritedRelationships = new CountSubClassOfAxiomsMetric(ontology).getValue();
	//double countRelationships = new CountLogicalAxiomsMetric(ontology).getValue();
	
	//MLIC: changed functions with boolean is deprecated
	/*
	float objectPropertyCount = ontology.getObjectPropertiesInSignature(true).size();
	float equivalentClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES, true);
	float disjointClasses = ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES, true);
	float classAssertion = ontology.getAxiomCount(AxiomType.CLASS_ASSERTION, true);
	float sameIndividual = ontology.getAxiomCount(AxiomType.SAME_INDIVIDUAL, true);
	float differentIndividual = ontology.getAxiomCount(AxiomType.DIFFERENT_INDIVIDUALS, true);
	*/
	double objectPropertyCount = ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();
	double equivalentClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES,OntologyUtility.ImportClosures(true));
	double disjointClasses = ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES,OntologyUtility.ImportClosures(true));
	//double classAssertion = ontology.getAxiomCount(AxiomType.CLASS_ASSERTION,OntologyUtility.ImportClosures(true));
	//double sameIndividual = ontology.getAxiomCount(AxiomType.SAME_INDIVIDUAL,OntologyUtility.ImportClosures(true));
	//double differentIndividual = ontology.getAxiomCount(AxiomType.DIFFERENT_INDIVIDUALS,OntologyUtility.ImportClosures(true));
	double nonInheritedRelationships = objectPropertyCount +  equivalentClasses + disjointClasses;// + classAssertion + sameIndividual + differentIndividual;

	//nonInheritedRelationships= countRelationships;
	// avoid a division by zero
	if (nonInheritedRelationships + inheritedRelationships == 0) {
	    return 0.0;
	} else {
	    return OntologyUtility.roundByGlobNK(nonInheritedRelationships / (nonInheritedRelationships + inheritedRelationships));
	}
    }

    @Override
    public String toString() {
	return "Relationship richness";
    }

}
