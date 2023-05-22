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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.dto.GoogleAuthenticationResponse;
import api.dto.LoginRequest;
import lombok.extern.slf4j.Slf4j;

import static java.lang.System.getenv;

@Service
@Slf4j
public class CustomHttpRequest {
	
	@Value("${KEYCLOAK_REALM}") 
	private String REALM;
	@Value("${KEYCLOAK_CLIENT_ID}") 
	private String CLIENT_ID;
	@Value("${KEYCLOAK_CLIENT_SECRET}") 
	private String CLIENT_SECRET;
	@Value("${KEYCLOAK_CLIENT_USER_ID}")
	private String CLIENT_USER_ID;
	@Value("${GOOGLE_CLIENT_ID}") 
	private String GG_CLIENT_ID;
	@Value("${GOOGLE_CLIENT_SECRET}") 
	private String GG_CLIENT_SECRET;
	@Value("${KEYCLOAK_ADMIN}") 
	private String ADMIN;
	@Value("${KEYCLOAK_ADMIN_PASSWORD}") 
	private String ADMIN_PWD;
	@Value("${HOST_NAME}")
	private String HOST_NAME;
	@Value("${APP_DOMAIN}")
	private String APP_DOMAIN;

	
//	private final String REALM = getenv("KEYCLOAK_REALM");
//	private final String CLIENT_ID = getenv("KEYCLOAK_CLIENT_ID");
//	private final String CLIENT_SECRET = getenv("KEYCLOAK_CLIENT_SECRET");
//	
//	private final String GG_CLIENT_ID = getenv("GOOGLE_CLIENT_ID");
//	private final String GG_CLIENT_SECRET = getenv("GOOGLE_CLIENT_SECRET");
//	
//	private final String ADMIN = getenv("KEYCLOAK_ADMIN");
//	private final String ADMIN_PWD = getenv("KEYCLOAK_ADMIN_PASSWORD");
	
	private final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:token-exchange";
	private final String SUBJECT_ISSUER = "google";
	private final String SUBJECT_TOKEN_TYPE = "urn:ietf:params:oauth:token-type:access_token";
	
	private HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();
	
	public String loginRequest(LoginRequest loginRequest) {
		log.info("Received username: "+loginRequest.getUsername() + " and pwd: "+loginRequest.getPassword());
		Map<String, String> formData = new HashMap<>();
		formData.put("grant_type", "password");
		formData.put("username", loginRequest.getUsername());
		formData.put("password", loginRequest.getPassword());

		String url = String.format("%s/realms/%s/protocol/openid-connect/token", HOST_NAME, REALM);
		
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
		
		String url = String.format("%s/realms/%s/protocol/openid-connect/token", HOST_NAME, REALM);
		
		return getToken(formData, CLIENT_ID, CLIENT_SECRET, url).body();
	}
	
	private String getGoogleAccessToken(String code) {
		Map<String, String> formData = new HashMap<>();
		formData.put("code", code);
		formData.put("grant_type", "authorization_code");
		String tokenExchangeUrl = String.format("%s/api/auth/token-exchange", APP_DOMAIN);
		formData.put("redirect_uri", tokenExchangeUrl);
		
		String url = "https://oauth2.googleapis.com/token";
		
		return getAccessToken(getToken(formData, GG_CLIENT_ID, GG_CLIENT_SECRET, url).body()).getAccess_token();
	}
	
	public boolean sendVerifyEmail(String username) {		
		String id = String.format("%s%s", CLIENT_USER_ID, username);
		log.info("keycloak userid: "+id);
		String response = loginRequest(new LoginRequest(ADMIN, ADMIN_PWD));
		String accessToken = getAccessToken(response).getAccess_token();
		if (accessToken==null) return false;
		
		String url = String.format("%s/admin/realms/%s/users/%s/send-verify-email", HOST_NAME, REALM, id);
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
		log.info("client id: "+id+" client secret: "+secret);
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
