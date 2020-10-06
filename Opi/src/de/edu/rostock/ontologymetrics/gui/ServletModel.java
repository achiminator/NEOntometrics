package de.edu.rostock.ontologymetrics.gui;

import java.util.ArrayList;
import java.util.List;

import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.OntologyMetric;
import de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.BaseMetric;

public class ServletModel {

	List<OntologyMetric> metrics;

	public ServletModel(List<OntologyMetric> pMetrics) {
		metrics = pMetrics;
	}

	public List<OntologyMetric> getBaseMetrics() {
		List<OntologyMetric> result = new ArrayList<OntologyMetric>();

		for (OntologyMetric metric : metrics) {
			if (metric instanceof BaseMetric) {
				result.add(metric);
			}
		}
		return result;
	}

}
