package main.java.neontometrics.api.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WrongURIExceptionMapper implements ExceptionMapper<WrongURIException>{
    
    public Response toResponse(WrongURIException exception) {
	ErrorMessage errorMessage = new ErrorMessage("The URI you provided does not link to a valid Ontology. Is the address correct?", Status.BAD_REQUEST.getStatusCode(),"You provided invalid information within the request. Please check your request.");
	return Response.status(Status.BAD_REQUEST).entity(errorMessage).build();
    }

}
