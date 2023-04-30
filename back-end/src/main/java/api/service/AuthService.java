package api.service;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.dto.KeycloakAuthenticationResponse;
import api.dto.LoginRequest;
import api.dto.RegisterRequest;
import api.dto.ResponseObject;
import api.model.User;
import api.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
	
	CustomHttpRequest httpRequest;
	RedditPasswordEncoder pwdEncoder;
	UserRepository userRepo;
	
	public KeycloakAuthenticationResponse login(LoginRequest request) {
		String response = httpRequest.loginRequest(request);
		ObjectMapper obj = new ObjectMapper();
		obj.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			KeycloakAuthenticationResponse json = obj.readValue(response, KeycloakAuthenticationResponse.class);
			if (json.getAccess_token() != null) {
				
			}
			return json;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ResponseEntity<ResponseObject> signup(RegisterRequest request) {
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setCreated(Instant.now());
		user.setEnabled(true);
		user.setPassword(pwdEncoder.encode(request.getPassword()));
		return ResponseEntity.ok(new ResponseObject("ok", "User Registration Successfully", userRepo.save(user)));
	}
	
	public User getCurrentUser() {
		JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String username = (String) token.getTokenAttributes().get("preferred_username");
        return userRepo.findByUsername(username)
        		.orElseThrow(() -> new UsernameNotFoundException("Username not found"));
	}

}
