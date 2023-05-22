package api.service;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import api.dto.LoginRequest;
import api.dto.RegisterRequest;
import api.dto.ResponseObject;
import api.model.User;
import api.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

	private CustomHttpRequest httpRequest;
	private RedditPasswordEncoder pwdEncoder;
	private UserRepository userRepo;
	
	public String login(LoginRequest request) {
		String response = httpRequest.loginRequest(request);
		
		if (response.equals("401")) return "Invalid user credentials";
		if (response.equals("400")) {			
			verifyEmail(request.getUsername());
			return "Account is not fully set up, please verify your email";
		}
		
		return response;
	}

	public ResponseEntity<ResponseObject> signup(RegisterRequest request) {
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setCreated(Instant.now());
		user.setEnabled(true);
		user.setPassword(pwdEncoder.encode(request.getPassword()));
		userRepo.save(user);
		verifyEmail(request.getUsername());			
		return ResponseEntity.ok(new ResponseObject("ok", 
				"User Registration Successfully. Please verify your email to fully set up your account", 
				userRepo.save(user)));
	}
	
	public User getCurrentUser() {
		JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String username = (String) token.getTokenAttributes().get("preferred_username");
        return userRepo.findByUsername(username)
        		.orElseThrow(() -> new UsernameNotFoundException("Username not found"));
	}

	private boolean verifyEmail(String id) {
		return httpRequest.sendVerifyEmail(id);
	}
}
