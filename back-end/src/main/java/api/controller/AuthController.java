package api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import api.dto.KeycloakAuthenticationResponse;
import api.dto.LoginRequest;
import api.dto.RegisterRequest;
import api.dto.ResponseObject;
import api.service.AuthService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
	
	private final AuthService authService;

	@PostMapping("/login")
	public KeycloakAuthenticationResponse login(@RequestBody LoginRequest request) {
		return authService.login(request);
	}
	
	@PostMapping("/signup")
	public ResponseEntity<ResponseObject> signup (@RequestBody RegisterRequest request) {
		return authService.signup(request);
	}

}
