package api.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.dto.CommentsDto;
import api.dto.ResponseObject;
import api.dto.VoteDto;
import api.exception.PostNotFoundException;
import api.mapper.CommentMapper;
import api.model.Comment;
import api.model.Post;
import api.model.User;
import api.model.VoteType;
import api.repository.CommentRepository;
import api.repository.PointRepository;
import api.repository.PostRepository;
import api.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CommentService {
	
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final PointRepository pointRepository;
	private final UserRepository userRepository;
	private final CommentMapper commentMapper;
	private final AuthService authService;
	private final PointService pointService;
	
	@Transactional
	public ResponseEntity<ResponseObject> save(CommentsDto commentsDto) {
		Post post = postRepository.findById(commentsDto.getPostId())
						.orElseThrow(() -> new PostNotFoundException(commentsDto.getPostId().toString()));
		
		Comment comment = commentMapper.map(commentsDto, authService.getCurrentUser(), post);
		
		pointRepository.save(pointService.map(new VoteDto(VoteType.UPVOTE, comment.getId()), comment));
		
		return ResponseEntity.status(HttpStatus.CREATED)
							 .body(new ResponseObject("created", "Add comment successfully", 
									 commentMapper.mapToDto(commentRepository.save(comment))));
	}

	@Transactional(readOnly = true)
	public ResponseEntity<List<CommentsDto>> getAllForPost(Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new PostNotFoundException(postId.toString()));
		List<CommentsDto> list = commentRepository.findByPost(post).stream()
								.map(commentMapper::mapToDto)
								.toList();
		return ResponseEntity.ok().body(list);
	}

	@Transactional(readOnly = true)
	public ResponseEntity<List<CommentsDto>> getAllForUser(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));
		List<CommentsDto> list = commentRepository.findByUser(user).stream()
								.map(commentMapper::mapToDto)
								.toList();
		return ResponseEntity.ok().body(list);
	}

}
