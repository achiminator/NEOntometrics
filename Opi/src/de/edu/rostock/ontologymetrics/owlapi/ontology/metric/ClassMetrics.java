package de.edu.rostock.ontologymetrics.owlapi.ontology.metric;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import com.google.common.collect.Multimap;

import de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility;

public abstract class ClassMetrics extends OntologyMetric {

    public Map<String, Object> getAllMetrics(OWLOntology ontology, IRI iri, Map<String, Object> metrics) {
	Map<String, Object> returnObject = new HashMap<>();
	returnObject.put("Class connectivity", classConnectivityMetric(ontology, iri));
	returnObject.put("Class fulness", classFulnessMetric(ontology, iri, metrics));
	returnObject.put("class importance", classImportanceMetric(ontology, iri, metrics));
	returnObject.put("Class children count", countClassChildrenMetric(ontology, iri));
	returnObject.put("class inheritance richness", classInheritenceRichnessMetric(ontology, iri, metrics));
	returnObject.put("class readability", classReadabilityMetric(ontology, iri));
	returnObject.put("Class relationship richness", classRelationshipRichnessMetric(ontology, iri));
	returnObject.put("Class instances count", countClassInstancesMetric(ontology, iri));
	returnObject.put("Class properties count", countClassPropertiesMetric(ontology, iri));

	return returnObject;
    }

    protected int countTotalInstancesOf(OWLClass cls, OWLOntology ontology) {
	int count = 0;
	if (cls != null) {
	    count = EntitySearcher.getIndividuals(cls, ontology).size();
	    for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls, ontology)) {
		count = count + countTotalInstancesOf(subClass.asOWLClass(), ontology);
	    }
	}
	return count;
    }

    public int classConnectivityMetric(OWLOntology ontology, IRI iri) {
	// TODO getConnectivity?! TEST

	int noInheritances = 0;

	// Instances of that class
	Iterator<OWLIndividual> cInstances = EntitySearcher
		.getInstances(OntologyUtility.getClass(ontology, iri), ontology).iterator();

	while (cInstances.hasNext()) {
	    // connection to others
	    Multimap<OWLObjectPropertyExpression, OWLIndividual> same = EntitySearcher
		    .getObjectPropertyValues(cInstances.next(), ontology);

	    for (OWLObjectPropertyExpression key : same.keySet()) {
		Collection<OWLIndividual> values = same.get(key);
		for (@SuppressWarnings("unused")
		OWLIndividual value : values) {
		    noInheritances++;
		}
	    }
	}
	return noInheritances;
    }

    public double classFulnessMetric(OWLOntology ontology, IRI iri, Map<String, Object> metrics) {
	Integer numberOfSubClasses = countClassChildrenMetric(ontology, iri);
	Integer classInstances = countClassInstancesMetric(ontology, iri);
	double actualNumberOfInstances = 0.0;

	if (numberOfSubClasses > 0)
	    actualNumberOfInstances = ((double) classInstances / (double) numberOfSubClasses);

	double expectedNumberOfInstances = (double) metrics.get("Average population");

	// avoid a division by zero
	if (expectedNumberOfInstances > 0) {
	    return 0.0;
	} else {
	    return OntologyUtility.roundByGlobNK((actualNumberOfInstances / expectedNumberOfInstances));
	}

    }

    public double classImportanceMetric(OWLOntology ontology, IRI iri, Map<String, Object> metrics) {
	int countTotalInstances = (int) metrics.get("Total individuals count");
	OWLClass cls = OntologyUtility.getClass(ontology, iri);
	int countInstances = countTotalInstancesOf(cls, ontology);

	// avoid a division by zero
	if (countTotalInstances == 0)
	    return 0.0;
	else
	    return OntologyUtility.roundByGlobNK((double) countInstances / (double) countTotalInstances);

    }

    public int countClassChildrenMetric(OWLOntology ontology, IRI iri) {
	int subClasses = 0;

	OWLClass cls = OntologyUtility.getClass(ontology, iri);

	if (cls != null) {
	    // MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    subClasses = EntitySearcher.getSubClasses(cls, ontology).size();
	    if (subClasses > 0) {
		for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls, ontology)) {
		    subClasses = subClasses + countClassChildrenMetric(ontology, subClass.asOWLClass().getIRI());
		}
	    }
	}

	return subClasses;

    }

    public double classInheritenceRichnessMetric(OWLOntology ontology, IRI iri, Map<String, Object> metrics) {

	int countClasses = (int) metrics.get("Total classes count");
	int countSubClasses = (int) countClassChildrenMetric(ontology, iri);

	// avoid a division by zero
	if (countSubClasses == 0) {
	    return 0.0;
	} else {
	    return OntologyUtility.roundByGlobNK((double) countClasses / (double) countSubClasses);
	}
    }

    public int classReadabilityMetric(OWLOntology ontology, IRI iri) {
	OWLClass cls = OntologyUtility.getClass(ontology, iri);
	int countAnnotations = 0;

	if (cls != null) {
	    // MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    for (OWLAnnotation annotation : EntitySearcher.getAnnotations(cls, ontology)) {
		if (annotation.getProperty().isLabel() || annotation.getProperty().isComment()) {
		    countAnnotations++;
		}
	    }
	}
	return countAnnotations;
    }

    public int classRelationshipRichnessMetric(OWLOntology ontology, IRI iri) {
	int noRelations = 0;
	OWLClass next = OntologyUtility.getClass(ontology, iri);
	for (OWLObjectProperty objProperty : next.getObjectPropertiesInSignature()) {
	    if (EntitySearcher.isFunctional(objProperty, ontology/* .getImports() */)) {
		noRelations += next.getObjectPropertiesInSignature().size();
	    }
	}
	return noRelations;
    }

    public int countClassInstancesMetric(OWLOntology ontology, IRI iri) {
	int instances = 0;

	OWLClass cls = OntologyUtility.getClass(ontology, iri);

	if (cls != null) {
	    // MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    instances = EntitySearcher.getIndividuals(cls, ontology).size();

	    // MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    if (!EntitySearcher.getSubClasses(cls, ontology).isEmpty()) {

		// MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
		for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls, ontology)) {
		    instances = instances + countClassInstancesMetric(ontology, subClass.asOWLClass().getIRI());
		}
	    }
	}

	return instances;
    }

    public int countClassPropertiesMetric(OWLOntology ontology, IRI iri) {
	int properties = 0;

	OWLClass cls = OntologyUtility.getClass(ontology, iri);

	if (cls != null) {
	    // MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    properties = cls.getDatatypesInSignature().size();

	    // MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
	    if (!EntitySearcher.getSubClasses(cls, ontology).isEmpty()) {

		// MLIC: Umstellung OWL API von 3.5 auf 4.2.3 (EntitySearcher)
		for (OWLClassExpression subClass : EntitySearcher.getSubClasses(cls, ontology)) {
		    properties = properties + countClassPropertiesMetric(ontology, subClass.asOWLClass().getIRI());
		}
	    }
	}

	return properties;
    }
}