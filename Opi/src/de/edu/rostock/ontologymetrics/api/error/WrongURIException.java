package de.edu.rostock.ontologymetrics.api.error;

public class WrongURIException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 5435768253795148868L;
    public String message;
    public WrongURIException() {
	super("Wrong URI Provided!");
    }

}
