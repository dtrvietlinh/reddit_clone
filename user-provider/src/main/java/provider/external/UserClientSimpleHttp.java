package provider.external;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.http.impl.client.CloseableHttpClient;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.models.KeycloakSession;

import com.fasterxml.jackson.core.type.TypeReference;

import lombok.SneakyThrows;
import provider.Constants;

public class UserClientSimpleHttp implements UserClient{

	private final CloseableHttpClient httpClient;
	private final String baseUrl;

	public UserClientSimpleHttp(KeycloakSession session, ComponentModel model) {
		this.httpClient = session.getProvider(HttpClientProvider.class).getHttpClient();
		this.baseUrl = model.get(Constants.BASE_URL);
	}

	@Override
	@SneakyThrows
	public List<RedditUser> getPeanuts(String search, int page, int size) {
		SimpleHttp simpleHttp = SimpleHttp.doGet(baseUrl, httpClient)
			.param("page", String.valueOf(page))
			.param("size", String.valueOf(size));
		if (search != null) {
			simpleHttp.param("search", search);
		}
		return simpleHttp.asJson(new TypeReference<>() {});
	}

	@Override
	@SneakyThrows
	public Integer getPeanutsCount() {
		String url = String.format("%s/count", baseUrl);
		String count = SimpleHttp.doGet(url, httpClient).asString();
		return Integer.valueOf(count);
	}

	@Override
	@SneakyThrows
	public RedditUser getPeanutById(String id) {
		String url = String.format("%s/%s", baseUrl, id);
		SimpleHttp.Response response = SimpleHttp.doGet(url, httpClient).asResponse();
		if (response.getStatus() == 404) {
			throw new WebApplicationException(response.getStatus());
		}
		if (response.getStatus()==204) return null;
		return response.asJson(RedditUser.class);
	}
	
	@Override
	@SneakyThrows
	public String getCredentialData(String id) {
		String url = String.format("%s/credentials/%s", baseUrl, id);
		SimpleHttp.Response response = SimpleHttp.doGet(url, httpClient).asResponse();
		if (response.getStatus() == 404) {
			throw new WebApplicationException(response.getStatus());
		}
		return response.asString();
	}

	@Override
	@SneakyThrows
	public Response updateCredentialData(UpdateCredentialsRequest credentialData) {
		String url = String.format("%s/credentials/update", baseUrl);
		int status = SimpleHttp.doPut(url, httpClient).json(credentialData).asStatus();
		return Response.status(status).build();
	}

	@Override
	@SneakyThrows
	public boolean addUser(RedditUser user) {
		String url = String.format("%s/add", baseUrl);
		int status = SimpleHttp.doPost(url, httpClient).json(user).asStatus();
		if (status!=200) {
			return false;
		}
		return true;
	}

	@Override
	@SneakyThrows
	public boolean deleteUser(String username) {
		String url = String.format("%s/delete/%s", baseUrl, username);
		int status = SimpleHttp.doDelete(url, httpClient).asStatus();
		if (status!=200) {
			return false;
		}
		return true;
	}
}
