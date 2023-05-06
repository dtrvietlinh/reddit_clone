package api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleAuthenticationResponse {
	private String access_token;
	private int expires_in;
	private String scope;
	private String token_type;
	private String id_token;	
}
