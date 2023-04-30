package api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.dto.KeycloakUpdateCredentialsRequest;
import api.dto.KeycloakUserResponse;
import api.model.User;
import api.repository.UserRepository;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/keycloak/user-provider")
@AllArgsConstructor
public class UserProviderController {

	private final UserRepository userRepo;
	
	@GetMapping("/{username}")
	public KeycloakUserResponse getUserById(@PathVariable String username) {
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found"));
		return KeycloakUserResponse.builder()
								   .username(user.getUsername())
								   .email(user.getEmail())
								   .created(user.getCreated().getEpochSecond())
								   .enabled(user.isEnabled())
								   .build();
	}
	
	@GetMapping
	public List<KeycloakUserResponse> getUsers(@RequestParam(required = false) String search, @RequestParam Integer page, @RequestParam Integer size) {
		
		if (search != null) {
			if (search.equals("*")) search="";
		} else {
			search="";
		}
		
		Page<User> listUser = userRepo.findByUsernameContaining(search, PageRequest.of(page, size));
		
		return listUser.stream()
					   .map(user -> KeycloakUserResponse.builder()
								   .username(user.getUsername())
								   .email(user.getEmail())
								   .created(user.getCreated().getEpochSecond())
								   .enabled(user.isEnabled())
								   .build())
					   .toList();
	}
	
	@GetMapping("/credentials/{username}")
	public String getCredentialData(@PathVariable String username) {
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found"));
		return user.getPassword();		
	}
	
	@PutMapping("/credentials/update")
	public int updateCredentialData(@RequestBody KeycloakUpdateCredentialsRequest credentials) {
		User user = userRepo.findByUsername(credentials.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("Username not found"));	
		user.setPassword(credentials.getPassword());
		userRepo.save(user);
		return 200;
	}
	
	@GetMapping("/count")
	public Integer getUsersCount() {
		return (int) userRepo.count();
	}
}
