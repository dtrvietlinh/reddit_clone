package provider.external;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserClient {
	@GET
	List<RedditUser> getPeanuts(@QueryParam("search") String search, @QueryParam("page") int page, @QueryParam("size") int size);

	@GET
	@Path("/count")
	Integer getPeanutsCount();

	@GET
	@Path("/{id}")
	RedditUser getPeanutById(@PathParam("id") String id);
	
	@GET
	@Path("/credentials/{id}")
	String getCredentialData(@PathParam("id") String id);
	
	@PUT
	@Path("/credentials/update")
	Response updateCredentialData(UpdateCredentialsRequest credentialData);
}
