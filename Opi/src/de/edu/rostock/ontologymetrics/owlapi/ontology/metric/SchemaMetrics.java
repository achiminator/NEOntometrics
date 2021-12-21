package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public class SchemaMetrics extends MetricCalculations implements Callable<SchemaMetrics> {

    private Map<String, Object> previousResults;

    public SchemaMetrics(Map<String, Object> previousResults, boolean withImports,
	    OWLOntology ontology) {
	super(ontology, withImports);
	this.previousResults = previousResults;

    }

    public SchemaMetrics call() {

	attributeRichnessMetric(ontology);
	schemaInheritenceRichnessMetric();
	schemaRelationshipRichnessMetric(ontology);
	attributeClassRatio(ontology);
	equivalenceRatioMetric(ontology);

	inverseRelationsRatioMetric(ontology);
	classRelationsRatioMetric(ontology);

	return this;
    }

    // TODO: Restructure for pure read
    public void attributeRichnessMetric(OWLOntology ontology) {
	double ret = 0;
	int classes = (int) previousResults.get("Classcount");
	int datapropertycount = ontology.getDataPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();

	if (classes == 0) {
	    ret = 0f;
	} else {
	    ret = (double) datapropertycount / (double) classes;
	}
	returnObject.put("Attributerichness", OntologyUtility.roundByGlobNK(ret));
    }

    /**
     * the inheritance richness of the schema (IRs) is defined as the average number
     * of subclasses per class
     * 
     * @return Metric Value
     */
    public double schemaInheritenceRichnessMetric() {
	double inheritanceRichness;
	int countSubClasses = (int) previousResults.get("SubClassOfaxiomscount");
	int countClasses = (int) previousResults.get("Classcount");

	// avoid a division by zero
	if (countClasses == 0) {
	    return 0.0;
	} else {
	    double ret = (double) countSubClasses / (double) countClasses;
	    inheritanceRichness = OntologyUtility.roundByGlobNK(ret);
	    return inheritanceRichness;
	}
    }

    // TODO: Replace things here by local variants
    public void schemaRelationshipRichnessMetric(OWLOntology ontology) {
	int inheritedRelationships = (int) previousResults.get("SubClassOfaxiomscount");
	int objectPropertyCount = ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();
	int equivalentClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES,
		OntologyUtility.ImportClosures(true));
	int disjointClasses = ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES, OntologyUtility.ImportClosures(true));

	int nonInheritedRelationships = objectPropertyCount + equivalentClasses + disjointClasses;// + classAssertion
												  // + sameIndividual
												  // +
												  // differentIndividual;
	double relationshipRichness = 0.0;
	// nonInheritedRelationships= countRelationships;
	// avoid a division by zero
	if (nonInheritedRelationships + inheritedRelationships == 0) {
	    relationshipRichness = 0.0;
	} else {
	    relationshipRichness = OntologyUtility.roundByGlobNK(
		    nonInheritedRelationships / (double) (nonInheritedRelationships + inheritedRelationships));
	}

	returnObject.put("Relationshiprichness", relationshipRichness);

    }

    public void attributeClassRatio(OWLOntology ontology) {
	int classCount = (int) previousResults.get("Classcount");
	int classesWithAttributes = 0;
	double attributeClassRatio = 0.0;
	// new
	// for (OWLClass cls : ontology.getClassesInSignature(true)) {
	for (OWLClass cls : ontology.getClassesInSignature(OntologyUtility.ImportClosures(true))) { // new

	    Collection<OWLClassExpression> superClses = EntitySearcher.getSuperClasses(cls, ontology);
	    for (OWLClassExpression superCls : superClses) {
		if (superCls.isAnonymous()) {
		    classesWithAttributes++;
		    continue;
		}
	    }
	}
	if (classCount == 0) {
	    attributeClassRatio = 0.0;
	} else
	    attributeClassRatio = OntologyUtility.roundByGlobNK((double) classesWithAttributes / (double) classCount);
	returnObject.put("Attributeclassratio", attributeClassRatio);

    }

    public void equivalenceRatioMetric(OWLOntology ontology) {
	int classes = (int) previousResults.get("Classcount");
	double equivalenceRatio;
	// MLIC: changed getAxiomCount with boolean is deprecated
	// float sameClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES,
	// true);
	int sameClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES, OntologyUtility.ImportClosures(true));

	if (classes == 0) {
	    equivalenceRatio = 0.0;
	} else {
	    equivalenceRatio = OntologyUtility.roundByGlobNK((double) sameClasses / (double) classes);
	}
	returnObject.put("Equivalenceratio", equivalenceRatio);

    }

    public void inverseRelationsRatioMetric(OWLOntology ontology) {
	double inverseRelationsRatio;
	int objectpropertycount = ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();
	int inverseobjectproperties = ontology.getAxiomCount(AxiomType.INVERSE_OBJECT_PROPERTIES,
		OntologyUtility.ImportClosures(true));
	int inversefunctionalproperties = ontology.getAxiomCount(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY,
		OntologyUtility.ImportClosures(true));
	int functionaldatapropertycount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_DATA_PROPERTY,
		OntologyUtility.ImportClosures(true));
	if (objectpropertycount + functionaldatapropertycount == 0) {
	    inverseRelationsRatio = 0.0;
	} else {
	    inverseRelationsRatio = OntologyUtility
		    .roundByGlobNK((double) (inverseobjectproperties + inversefunctionalproperties)
			    / (double) (objectpropertycount + functionaldatapropertycount));
	}

	returnObject.put("Inverserelationsratio", inverseRelationsRatio);

    }

    public void classRelationsRatioMetric(OWLOntology ontology) {
	double classRelationRatio;
	int classes = (int) previousResults.get("Classcount");
	int subClasses = (int) previousResults.get("SubClassOfaxiomscount");
	int objectPropertyCount = ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();
	int equivalentClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES,
		OntologyUtility.ImportClosures(true));
	int disjointClasses = ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES, OntologyUtility.ImportClosures(true));
	int relations = objectPropertyCount + equivalentClasses + disjointClasses + subClasses;// + classAssertion +
											       // sameIndividual +
											       // differentIndividual;
	// relations=countRelationships;
	if (relations == 0) {
	    classRelationRatio = 0.0;
	} else {
	    classRelationRatio = OntologyUtility.roundByGlobNK((double) classes / (double) relations);
	}
	returnObject.put("Classrelationratio", classRelationRatio);

    }

}
