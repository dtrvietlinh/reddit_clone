package api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import api.dto.CommentsDto;
import api.model.Comment;
import api.model.Post;
import api.model.User;


@Mapper
public interface CommentMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "text", source = "commentsDto.text")
	@Mapping(target = "post", source = "post")
	@Mapping(target = "user", source = "user")
	@Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
	@Mapping(target = "points", constant = "1")
	Comment map(CommentsDto commentsDto, User user, Post post);
	
	@Mapping(target = "postId", expression = "java(comment.getPost().getPostId())")
	@Mapping(target = "username", expression = "java(comment.getUser().getUsername())")
	CommentsDto mapToDto(Comment comment);
}
