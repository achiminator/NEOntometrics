package main.java.neontometrics.api.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class InvalidOntologyExceptionMapper implements ExceptionMapper<InvalidOntologyException>{
    public Response toResponse(InvalidOntologyException exception) {
	ErrorMessage errorMessage = new ErrorMessage("The overloaded Ontology is not valid! Please check!", Status.BAD_REQUEST.getStatusCode(), "");
	return Response.status(Status.BAD_REQUEST).entity(errorMessage).build();
    }

}
