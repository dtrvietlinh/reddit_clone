package api.mapper;

import api.dto.SubredditDto;
import api.model.Subreddit;
import api.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-05T17:12:06+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 19.0.1 (Oracle Corporation)"
)
@Component
public class SubredditMapperImpl implements SubredditMapper {

    @Override
    public SubredditDto mapToDto(Subreddit subreddit) {
        if ( subreddit == null ) {
            return null;
        }

        SubredditDto.SubredditDtoBuilder subredditDto = SubredditDto.builder();

        subredditDto.name( subreddit.getName() );
        subredditDto.moderator( subredditUserUsername( subreddit ) );
        subredditDto.id( subreddit.getId() );
        subredditDto.description( subreddit.getDescription() );

        subredditDto.numberOfPosts( mapPosts(subreddit.getPosts()) );

        return subredditDto.build();
    }

    @Override
    public Subreddit map(SubredditDto subredditDto, User user) {
        if ( subredditDto == null && user == null ) {
            return null;
        }

        Subreddit.SubredditBuilder subreddit = Subreddit.builder();

        if ( subredditDto != null ) {
            subreddit.name( subredditDto.getName() );
            subreddit.id( subredditDto.getId() );
            subreddit.description( subredditDto.getDescription() );
        }
        subreddit.user( user );
        subreddit.createdDate( java.time.Instant.now() );

        return subreddit.build();
    }

    private String subredditUserUsername(Subreddit subreddit) {
        if ( subreddit == null ) {
            return null;
        }
        User user = subreddit.getUser();
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
