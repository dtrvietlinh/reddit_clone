package api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.dto.CommentsDto;
import api.dto.ResponseObject;
import api.service.CommentService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/comments")
@AllArgsConstructor
public class CommentController {
	
	private final CommentService commentService;
	
	@PostMapping
	public ResponseEntity<ResponseObject> addComment(@RequestBody CommentsDto commentsDto) {
		return commentService.save(commentsDto);
	}
	
	@GetMapping("/by-post/{postId}")
	public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@PathVariable Long postId) {
		return commentService.getAllForPost(postId);
	}
	
	@GetMapping("/by-user/{username}")
	public ResponseEntity<List<CommentsDto>> getAllCommentsForUser(@PathVariable String username) {
		return commentService.getAllForUser(username);
	}
	
	
}
