package api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.dto.PostRequest;
import api.dto.PostResponse;
import api.dto.ResponseObject;
import api.service.PostService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("api/posts")
public class PostController {
	private final PostService postService;
	
	@PostMapping
	public ResponseEntity<ResponseObject> createPost(@RequestBody PostRequest postRequest) {
		return postService.save(postRequest);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ResponseObject> getPost(@PathVariable Long id) {
		return postService.getPost(id);
	}

	@GetMapping("/")
	public ResponseEntity<List<PostResponse>> getAllPosts() {
		return postService.getAll();
	}
	
	@GetMapping("/by-subreddit/{id}")
	public ResponseEntity<List<PostResponse>> getPostsBySubreddit(@PathVariable Long id) {
		return postService.getPostsBySubreddit(id);
	}

	@GetMapping("/by-user/{username}")
	public ResponseEntity<List<PostResponse>> getPostsByUsername(@PathVariable String username) {
		return postService.getPostsByUsername(username);
	}
}
