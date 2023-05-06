package api.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.dto.GoogleAuthenticationResponse;
import api.dto.LoginRequest;

import static java.lang.System.getenv;

@Service
public class CustomHttpRequest {
	
	private final static String REALM = getenv("KEYCLOAK_REALM");
	private final static String CLIENT_ID = getenv("KEYCLOAK_CLIENT_ID");
	private final static String CLIENT_SECRET = getenv("KEYCLOAK_CLIENT_SECRET");
	
	private final static String GG_CLIENT_ID = getenv("GOOGLE_CLIENT_ID");
	private final static String GG_CLIENT_SECRET = getenv("GOOGLE_CLIENT_SECRET");
	
	private final static String ADMIN = getenv("KEYCLOAK_ADMIN");
	private final static String ADMIN_PWD = getenv("KEYCLOAK_ADMIN_PASSWORD");
	
	private final static String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:token-exchange";
	private final static String SUBJECT_ISSUER = "google";
	private final static String SUBJECT_TOKEN_TYPE = "urn:ietf:params:oauth:token-type:access_token";
	
	private HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();
	
	public String loginRequest(LoginRequest loginRequest) {
		Map<String, String> formData = new HashMap<>();
		formData.put("grant_type", "password");
		formData.put("username", loginRequest.getUsername());
		formData.put("password", loginRequest.getPassword());

		String url = String.format("http://localhost:8180/realms/%s/protocol/openid-connect/token", REALM);
		
		HttpResponse<String> response = getToken(formData, CLIENT_ID, CLIENT_SECRET, url);
		
		if (response.statusCode()==400) return "400";
		if (response.statusCode()==401) return "401";
		return response.body();
	}
	
	public String tokenExchange(String code) {
		
		String accessToken = getGoogleAccessToken(code);
		
		Map<String, String> formData = new HashMap<>();
		formData.put("grant_type", GRANT_TYPE);
		formData.put("subject_token", accessToken);
		formData.put("subject_issuer", SUBJECT_ISSUER);
		formData.put("subject_token_type", SUBJECT_TOKEN_TYPE);
		
		String url = String.format("http://localhost:8180/realms/%s/protocol/openid-connect/token", REALM);
		
		return getToken(formData, CLIENT_ID, CLIENT_SECRET, url).body();
	}
	
	private String getGoogleAccessToken(String code) {
		Map<String, String> formData = new HashMap<>();
		formData.put("code", code);
		formData.put("grant_type", "authorization_code");
		formData.put("redirect_uri", "http://localhost:8080/api/auth/token-exchange");
		
		String url = "https://oauth2.googleapis.com/token";
		
		return getAccessToken(getToken(formData, GG_CLIENT_ID, GG_CLIENT_SECRET, url).body()).getAccess_token();
	}
	
	public boolean sendVerifyEmail(String id) {
		String response = loginRequest(new LoginRequest(ADMIN, ADMIN_PWD));
		String accessToken = getAccessToken(response).getAccess_token();
		if (accessToken==null) return false;
		
		String url = String.format("http://localhost:8180/admin/realms/%s/users/%s/send-verify-email", REALM, id);
		HttpRequest request = HttpRequest.newBuilder()
										 .uri(URI.create(url))
										 .header("Content-Type", "application/json")
										 .header("Authorization", "Bearer "+ accessToken)
										 .PUT(BodyPublishers.ofString(""))
										 .build();
		try {
			httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			return true;
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private HttpResponse<String> getToken(Map<String, String> formData, String id, String secret, String url) {
		formData.put("client_id", id);
		formData.put("client_secret", secret);		
		
		HttpRequest request = HttpRequest.newBuilder()
										 .uri(URI.create(url))
										 .header("Content-Type", "application/x-www-form-urlencoded")
										 .POST(BodyPublishers.ofString(getFormDataAsString(formData)))
										 .build();
		try {
			return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getFormDataAsString(Map<String, String> formData) {
	    StringBuilder formBodyBuilder = new StringBuilder();
	    for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
	        if (formBodyBuilder.length() > 0) {
	            formBodyBuilder.append("&");
	        }
	        formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
	        formBodyBuilder.append("=");
	        formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
	    }
	    return formBodyBuilder.toString();
	}
		
	private GoogleAuthenticationResponse getAccessToken(String response) {
		ObjectMapper obj = new ObjectMapper();
		obj.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return obj.readValue(response, GoogleAuthenticationResponse.class);			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
