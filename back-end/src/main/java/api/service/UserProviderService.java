package api.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import api.dto.KeycloakUpdateCredentialsRequest;
import api.dto.KeycloakUserResponse;
import api.model.User;
import api.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserProviderService {

	private final UserRepository userRepo;
	private final RedditPasswordEncoder pwdEncoder;

	public ResponseEntity<KeycloakUserResponse> getUserById(String username) {
		Optional<User> opt = userRepo.findByUsername(username);
				
		if (!opt.isPresent()) return ResponseEntity.status(204).body(null);
		
		User user = opt.get();
		return ResponseEntity.ok(KeycloakUserResponse.builder()
								   .username(user.getUsername())
								   .email(user.getEmail())
								   .created(user.getCreated().getEpochSecond())
								   .enabled(user.isEnabled())
								   .build());
	}

	public List<KeycloakUserResponse> getUsers(String search, Integer page, Integer size) {
		if (search != null) {
			if (search.equals("*"))
				search = "";
		} else {
			search = "";
		}
		Page<User> listUser = userRepo.findByUsernameContaining(search, PageRequest.of(page, size));
		return listUser.stream().map(user -> KeycloakUserResponse.builder()
																.username(user.getUsername())
																.email(user.getEmail())
																.created(user.getCreated().getEpochSecond())
																.enabled(user.isEnabled()).build())
																.toList();
	}

	public String getCredentialData(String username) {
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found"));
		return user.getPassword();
	}

	public int updateCredentialData(KeycloakUpdateCredentialsRequest credentials) {
		User user = userRepo.findByUsername(credentials.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("Username not found"));
		user.setPassword(credentials.getPassword());
		userRepo.save(user);
		return 200;
	}
	
	public ResponseEntity<String> addUser(KeycloakUserResponse kUser) {
		User user = new User();
		user.setUsername(kUser.getUsername());
		user.setEmail(kUser.getEmail());
		user.setEnabled(kUser.isEnabled());
		user.setCreated(Instant.ofEpochSecond(kUser.getCreated()));
		user.setPassword(pwdEncoder.encode("test"));
		userRepo.save(user);
		return ResponseEntity.ok("OK");
	}
	
	public ResponseEntity<String> deleteUser(String username) {
		Optional<User> user = userRepo.findByUsername(username);
		if (!user.isPresent()) return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
		userRepo.delete(user.get());
		return ResponseEntity.ok("deleted");
	}
}
