package api.mapper;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import api.dto.SubredditDto;
import api.model.Post;
import api.model.Subreddit;
import api.model.User;

@Mapper
public interface SubredditMapper {
	
	@Mapping(target = "name", source = "subreddit.name")
	@Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
	@Mapping(target = "moderator", source = "user.username")
	SubredditDto mapToDto(Subreddit subreddit);

	default Integer mapPosts(List<Post> numberOfPosts) {
		return numberOfPosts.size();
	}
	
	@InheritInverseConfiguration
	@Mapping(target = "name", source = "subredditDto.name")
	@Mapping(target = "posts", ignore = true)
	@Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
	@Mapping(target = "user", source = "user")
	Subreddit map(SubredditDto subredditDto, User user);
	
}
