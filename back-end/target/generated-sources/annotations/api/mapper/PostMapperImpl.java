package api.mapper;

import api.dto.PostRequest;
import api.dto.PostResponse;
import api.model.Post;
import api.model.Subreddit;
import api.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-05T17:12:07+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 19.0.1 (Oracle Corporation)"
)
@Component
public class PostMapperImpl extends PostMapper {

    @Override
    public Post map(PostRequest postRequest, Subreddit subreddit, User user) {
        if ( postRequest == null && subreddit == null && user == null ) {
            return null;
        }

        Post.PostBuilder post = Post.builder();

        if ( postRequest != null ) {
            post.description( postRequest.getDescription() );
            post.postId( postRequest.getPostId() );
            post.postName( postRequest.getPostName() );
            post.url( postRequest.getUrl() );
        }
        post.subreddit( subreddit );
        post.user( user );
        post.createDate( java.time.Instant.now() );
        post.voteCount( 1 );

        return post.build();
    }

    @Override
    public PostResponse mapToDto(Post post) {
        if ( post == null ) {
            return null;
        }

        PostResponse.PostResponseBuilder postResponse = PostResponse.builder();

        postResponse.id( post.getPostId() );
        postResponse.subredditName( postSubredditName( post ) );
        postResponse.username( postUserUsername( post ) );
        postResponse.postName( post.getPostName() );
        postResponse.url( post.getUrl() );
        postResponse.description( post.getDescription() );
        postResponse.voteCount( post.getVoteCount() );

        postResponse.commentCount( commentCount(post) );
        postResponse.duration( duration(post) );

        return postResponse.build();
    }

    private String postSubredditName(Post post) {
        if ( post == null ) {
            return null;
        }
        Subreddit subreddit = post.getSubreddit();
        if ( subreddit == null ) {
            return null;
        }
        String name = subreddit.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String postUserUsername(Post post) {
        if ( post == null ) {
            return null;
        }
        User user = post.getUser();
        if ( user == null ) {
            return null;
        }
        String username = user.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }
}
