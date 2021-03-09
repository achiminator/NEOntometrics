package de.edu.rostock.ontologymetrics.gui;

public class GuiHelper {
    public static String removeSpecialCharacters(String iri) {
	iri = iri.replace("#", "");
	iri = iri.replace(".", "");
	iri = iri.replace("/", "");
	iri = iri.replace(":", "");
	iri = iri.replace("-", "");
	return iri;
    }
}
