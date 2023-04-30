package api.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentsDto {
	private Long id;
	private Long postId;
	private String text;
	private String username;
	private Integer points;
	private Instant createdDate;
}
