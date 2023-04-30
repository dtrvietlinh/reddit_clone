package provider.external;

import lombok.Data;

@Data
public class RedditUser {
	private String username;
	private String email;
	private Long created;
	private boolean enabled;
}
