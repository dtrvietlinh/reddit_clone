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

import api.dto.LoginRequest;

@Service
public class CustomHttpRequest {
	
	private HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();
	
	public String loginRequest(LoginRequest loginRequest) {
		Map<String, String> formData = new HashMap<>();
		formData.put("grant_type", "password");
		formData.put("client_id", "demo-client");
		formData.put("client_secret", "Z3IaRD8ZQo3T1IK2FgJ7C5xpc3FSxUET");
		formData.put("username", loginRequest.getUsername());
		formData.put("password", loginRequest.getPassword());
		
		HttpRequest request = HttpRequest.newBuilder()
										 .uri(URI.create("http://localhost:8180/realms/keycloak-demo/protocol/openid-connect/token"))
										 .header("Content-Type", "application/x-www-form-urlencoded")
										 .POST(BodyPublishers.ofString(getFormDataAsString(formData)))
										 .build();
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			return response.body();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void setContext(String accessToken) {
		
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
}
