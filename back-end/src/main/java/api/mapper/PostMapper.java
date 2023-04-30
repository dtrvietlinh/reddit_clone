package api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;

import api.dto.PostRequest;
import api.dto.PostResponse;
import api.model.Post;
import api.model.Subreddit;
import api.model.User;
import api.repository.CommentRepository;
import api.repository.VoteRepository;
import api.service.AuthService;

@Mapper
public abstract class PostMapper {
	
	@Autowired
    private CommentRepository commentRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthService authService;
	
	@Mapping(target = "createDate", expression="java(java.time.Instant.now())")
	@Mapping(target = "user", source = "user")
	@Mapping(target = "subreddit", source = "subreddit")
	@Mapping(target = "description", source = "postRequest.description")
	@Mapping(target = "voteCount", constant = "1")
	public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);
	
	@Mapping(target = "id", source = "postId")
	@Mapping(target = "subredditName", source = "subreddit.name")
	@Mapping(target = "username", source = "user.username")
	@Mapping(target = "commentCount", expression = "java(commentCount(post))")
	@Mapping(target = "duration", expression = "java(duration(post))")
	public abstract PostResponse mapToDto(Post post);
	
	Integer commentCount(Post post) {
		return commentRepository.findByPost(post).size();
	}
	
	String duration(Post post) {
		String timeAgo = new PrettyTime().formatDuration(post.getCreateDate());
		return timeAgo.equals("") ? "Just now" : timeAgo + " ago";
	}
}
