package api.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.dto.LoginRequest;
import api.dto.RegisterRequest;
import api.dto.ResponseObject;
import api.service.AuthService;
import api.service.CustomHttpRequest;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	private final CustomHttpRequest httpRequest;

	@PostMapping("/login")
	public String login(@RequestBody LoginRequest request) {
		return authService.login(request);
	}
	
	@PostMapping("/signup")
	public ResponseEntity<ResponseObject> signup (@RequestBody RegisterRequest request) {
		return authService.signup(request);
	}
	
	@GetMapping("/token-exchange")
	public String redirectUri(@RequestParam Map<String, String> form) {
		return httpRequest.tokenExchange(form.get("code"));
	}

}
