package provider.external;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateCredentialsRequest {
	private String username;
	private String password;
}