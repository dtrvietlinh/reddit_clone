package api.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.dto.PostRequest;
import api.dto.PostResponse;
import api.dto.ResponseObject;
import api.dto.VoteDto;
import api.exception.PostNotFoundException;
import api.exception.SubredditNotFoundException;
import api.mapper.PostMapper;
import api.model.Post;
import api.model.Subreddit;
import api.model.User;
import api.model.VoteType;
import api.repository.PostRepository;
import api.repository.SubredditRepository;
import api.repository.UserRepository;
import api.repository.VoteRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class PostService {
	
	private final SubredditRepository subredditRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final VoteRepository voteRepository;
	private final VoteService voteService;
	private final AuthService authService;
	private final PostMapper postMapper;

	public ResponseEntity<ResponseObject> save(PostRequest postRequest) {
		Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
								.orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));
		
		User currentUser = authService.getCurrentUser();
		
		Post post = postRepository.save(postMapper.map(postRequest, subreddit, currentUser));
		
		voteRepository.save(voteService.map(new VoteDto(VoteType.UPVOTE, post.getPostId()), post));
		
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ResponseObject("Created!", "Post has been created", postMapper.mapToDto(post)));

	}

	public ResponseEntity<ResponseObject> getPost(Long id) {
		Post post = postRepository.findById(id)
						.orElseThrow(() -> new PostNotFoundException("Can not found post with id " + id));
		
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ResponseObject("ok", "Post found",
						postMapper.mapToDto(post)));
	}

	public ResponseEntity<List<PostResponse>> getAll() {
		List<PostResponse> list = postRepository.findAll().stream()
									.map(postMapper::mapToDto)
									.toList();
		
		return ResponseEntity.ok().body(list);
	}

	public ResponseEntity<List<PostResponse>> getPostsBySubreddit(Long id) {
		Subreddit subreddit = subredditRepository.findById(id)
				.orElseThrow(() -> new SubredditNotFoundException("Can not found subreddit with id " + id));
		
		List<PostResponse> list = postRepository.findBySubreddit(subreddit).stream()
									.map(postMapper::mapToDto)
									.toList();
		
		return ResponseEntity.ok().body(list);
	}

	public ResponseEntity<List<PostResponse>> getPostsByUsername(String name) {
		User user = userRepository.findByUsername(name)
				.orElseThrow(() -> new UsernameNotFoundException("Can not found username with " + name));
		
		List<PostResponse> list = postRepository.findAllByUser(user).stream()
									.map(postMapper::mapToDto)
									.toList();
		
		return ResponseEntity.ok().body(list);
	}

}
