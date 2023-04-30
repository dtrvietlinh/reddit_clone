package api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.dto.PostResponse;
import api.dto.ResponseObject;
import api.dto.VoteDto;
import api.service.VoteService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/votes")
@AllArgsConstructor
public class VoteController {
	private final VoteService voteService;
	
	@PostMapping
	public ResponseEntity<ResponseObject> vote(@RequestBody VoteDto voteDto) {
		return voteService.vote(voteDto);
	}
	
	@GetMapping("/upvoted")
	public List<PostResponse> getUpvotedPosts(@RequestParam(defaultValue = "1") int direction) {
		return voteService.getVotedPosts(direction);
	}
	
	@GetMapping("/downvoted")
	public List<PostResponse> getDownvotedPosts(@RequestParam(defaultValue = "-1") int direction) {
		return voteService.getVotedPosts(direction);
	}
}
