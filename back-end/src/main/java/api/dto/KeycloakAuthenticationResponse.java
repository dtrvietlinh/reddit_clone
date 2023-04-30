package api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeycloakAuthenticationResponse {
	private String access_token;
	private int expires_in;
	private int refresh_expires_in;
	private String refresh_token;
	private String token_type;
	private int not_before_policy;
	private String session_state;
	private String scope;
}
