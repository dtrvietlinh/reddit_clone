package api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.dto.KeycloakUpdateCredentialsRequest;
import api.dto.KeycloakUserResponse;
import api.repository.UserRepository;
import api.service.UserProviderService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/keycloak/user-provider")
@AllArgsConstructor
public class UserProviderController {

	private final UserRepository userRepo;
	
	private final UserProviderService userService;
	
	@GetMapping("/{username}")
	public ResponseEntity<KeycloakUserResponse> getUserById(@PathVariable String username) {
		return userService.getUserById(username);
	}
	
	@GetMapping
	public List<KeycloakUserResponse> getUsers(@RequestParam(required = false) String search, @RequestParam Integer page, @RequestParam Integer size) {
		return userService.getUsers(search, page, size);
	}
	
	@GetMapping("/credentials/{username}")
	public String getCredentialData(@PathVariable String username) {
		return userService.getCredentialData(username);		
	}
	
	@PutMapping("/credentials/update")
	public int updateCredentialData(@RequestBody KeycloakUpdateCredentialsRequest credentials) {
		return userService.updateCredentialData(credentials);
	}
	
	@GetMapping("/count")
	public Integer getUsersCount() {
		return (int) userRepo.count();
	}
	
	@PostMapping("/add")
	public ResponseEntity<String> addUser(@RequestBody KeycloakUserResponse kUser) {
		return userService.addUser(kUser);
	}
	
	@DeleteMapping("/delete/{username}")
	public ResponseEntity<String> deleteUser(@PathVariable String username) {
		return userService.deleteUser(username);
	}
}
