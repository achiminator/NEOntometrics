package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.graphbasemetric.GraphParser;

public class SchemaMetrics extends OntologyMetric {

    private double attributeRichness;
    private double inheritanceRichness;
    private double relationshipRichness;
    private double attributeClassRatio;
    private double equivalenceRatio;
    private double axiomClassRatio;
    private double inverseRelationsRatio;
    private double classRelationRatio;

    private BaseMetric baseMetric;
    private ClassAxiomsMetric classAxiomsMetric;

    public SchemaMetrics(BaseMetric baseMetric, ClassAxiomsMetric classAxiomsMetric) {
	this.baseMetric = baseMetric;
	this.classAxiomsMetric = classAxiomsMetric;
    }

    public Map<String, Object> getAllMetrics() {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("Attributerichness", attributeRichness);
	returnObject.put("Inheritancerichness", inheritanceRichness);
	returnObject.put("Relationshiprichness", relationshipRichness);
	returnObject.put("Attributeclassratio", attributeClassRatio);
	returnObject.put("Equivalenceratio", equivalenceRatio);
	returnObject.put("Axiomclassratio", axiomClassRatio);
	returnObject.put("Inverserelationsratio", inverseRelationsRatio);
	returnObject.put("Classrelationratio", classRelationRatio);

	return returnObject;
    }
    public Map<String, Object> calculateAllMetrics(OWLOntology ontology, GraphParser parserI) {
	Map<String, Object> returnObject = new LinkedHashMap<>();
	returnObject.put("Attributerichness", attributeRichnessMetric(ontology, parserI));
	returnObject.put("Inheritancerichness", schemaInheritenceRichnessMetric());
	returnObject.put("Relationshiprichness", schemaRelationshipRichnessMetric( ontology));
	returnObject.put("Attributeclassratio", attributeClassRatio(ontology));
	returnObject.put("Equivalenceratio", equivalenceRatioMetric(ontology));
	returnObject.put("Axiomclassratio", axiomClassRatioMetric());
	returnObject.put("Inverserelationsratio", inverseRelationsRatioMetric(ontology));
	returnObject.put("Classrelationratio", classRelationsRatioMetric(ontology));
	
	return returnObject;
    }

    // TODO: Restructure for pure read
    public double attributeRichnessMetric(OWLOntology ontology, GraphParser parserI) {
	double ret = 0;
	float classes = parserI.getNoClasses();
	int datapropertycount = ontology.getDataPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();

	if (classes == 0) {
	    ret = 0f;
	} else {
	    ret = (double) datapropertycount / (double) classes;
	    myLogger.debug("datapropertycount/classes="
		    + datapropertycount
		    + "/"
		    + classes);
	}
	attributeRichness = OntologyUtility.roundByGlobNK(ret);
	return attributeRichness;
    }

    /**
     * the inheritance richness of the schema (IRs) is defined as the average number
     * of subclasses per class
     * 
     * @return Metric Value
     */
    public double schemaInheritenceRichnessMetric() {
	int countSubClasses = classAxiomsMetric.getSubClassOfAxiomsCount();
	int countClasses = baseMetric.getTotalClassesCount();

	// avoid a division by zero
	if (countClasses == 0) {
	    return 0.0;
	} else {
	    myLogger.debug("countSubClasses: "
		    + countSubClasses);
	    myLogger.debug("countClasses: "
		    + countClasses);
	    myLogger.debug("countSubClasses / countClasses: "
		    + countSubClasses / countClasses);
	    double ret = (double) countSubClasses / (double) countClasses;
	    inheritanceRichness = OntologyUtility.roundByGlobNK(ret);
	    return inheritanceRichness;
	}
    }

    // TODO: Replace things here by local variants
    public double schemaRelationshipRichnessMetric(OWLOntology ontology) {
	int inheritedRelationships = classAxiomsMetric.getSubClassOfAxiomsCount();
	int objectPropertyCount = ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(true)).size();
	int equivalentClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES,
		OntologyUtility.ImportClosures(true));
	int disjointClasses = ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES, OntologyUtility.ImportClosures(true));

	int nonInheritedRelationships = objectPropertyCount + equivalentClasses + disjointClasses;// + classAssertion
												  // + sameIndividual
												  // +
												  // differentIndividual;

	// nonInheritedRelationships= countRelationships;
	// avoid a division by zero
	if (nonInheritedRelationships + inheritedRelationships == 0) {
	    relationshipRichness = 0.0;
	} else {
	    relationshipRichness = OntologyUtility
		    .roundByGlobNK(nonInheritedRelationships / (double) (nonInheritedRelationships + inheritedRelationships));
	}
	return relationshipRichness;
    }

    public double attributeClassRatio(OWLOntology ontology) {
	int classCount = baseMetric.getTotalClassesCount();
	int classesWithAttributes = 0;

	myLogger.debug("classes: "
		+ classCount);

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

	myLogger.debug("result: "
		+ attributeClassRatio);
	return attributeClassRatio;
    }

    public double equivalenceRatioMetric( OWLOntology ontology) {
	int classes = baseMetric.getTotalClassesCount();
	// MLIC: changed getAxiomCount with boolean is deprecated
	// float sameClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES,
	// true);
	int sameClasses = ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES, OntologyUtility.ImportClosures(true));

	if (classes == 0) {
	    equivalenceRatio = 0.0;
	} else {
	    equivalenceRatio = OntologyUtility.roundByGlobNK((double) sameClasses / (double) classes);
	}
	return equivalenceRatio;
    }

    public double axiomClassRatioMetric() {
	int axioms = baseMetric.getAxioms();
	int classes = baseMetric.getTotalClassesCount();
	if (classes == 0) {
	    axiomClassRatio = 0.0;
	} else {
	    axiomClassRatio = OntologyUtility.roundByGlobNK((double) axioms / (double) classes);
	}
	return axiomClassRatio;
    }

    public double inverseRelationsRatioMetric(OWLOntology ontology) {
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
			    /(double) (objectpropertycount + functionaldatapropertycount));
	}
	return inverseRelationsRatio;
    }

    public double classRelationsRatioMetric(OWLOntology ontology) {
	int classes = baseMetric.getTotalClassesCount();
	int subClasses = classAxiomsMetric.getSubClassOfAxiomsCount();
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
	return classRelationRatio;
    }

}
