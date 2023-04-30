package api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeycloakUpdateCredentialsRequest {
	private String username;
	private String password;
	
}
