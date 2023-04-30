package api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
	private Long id;
	private String postName;
	private String url;
	private String description;
	private String username;
	private String subredditName;
	private Integer voteCount;
	private Integer commentCount;
	private String duration;
}
