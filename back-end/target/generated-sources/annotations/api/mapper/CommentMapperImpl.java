package api.mapper;

import api.dto.CommentsDto;
import api.model.Comment;
import api.model.Post;
import api.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-21T16:47:14+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 19.0.1 (Oracle Corporation)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment map(CommentsDto commentsDto, User user, Post post) {
        if ( commentsDto == null && user == null && post == null ) {
            return null;
        }

        Comment.CommentBuilder comment = Comment.builder();

        if ( commentsDto != null ) {
            comment.text( commentsDto.getText() );
        }
        comment.user( user );
        comment.post( post );
        comment.createdDate( java.time.Instant.now() );
        comment.points( 1 );

        return comment.build();
    }

    @Override
    public CommentsDto mapToDto(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentsDto.CommentsDtoBuilder commentsDto = CommentsDto.builder();

        commentsDto.id( comment.getId() );
        commentsDto.text( comment.getText() );
        commentsDto.points( comment.getPoints() );
        commentsDto.createdDate( comment.getCreatedDate() );

        commentsDto.postId( comment.getPost().getPostId() );
        commentsDto.username( comment.getUser().getUsername() );

        return commentsDto.build();
    }
}
