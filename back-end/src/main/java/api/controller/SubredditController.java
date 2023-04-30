package api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.dto.ResponseObject;
import api.dto.SubredditDto;
import api.service.SubredditService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/subreddit")
@AllArgsConstructor
@Slf4j
public class SubredditController {
	
	private final SubredditService subredditService;
	
	@PostMapping
	public ResponseEntity<ResponseObject> createSubreddit(@RequestBody SubredditDto subredditDto) {
		SubredditDto newSub = subredditService.save(subredditDto);
		return newSub != null ? 
				ResponseEntity.status(HttpStatus.CREATED)
							  .body(new ResponseObject("Created!", "Subreddit has been created", newSub)):
				ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
							  .body(new ResponseObject("Error", "Failed", ""));
	}
	
	@GetMapping
	public ResponseEntity<ResponseObject> getAllSubreddits() {
		return ResponseEntity
				.ok()
				.body(new ResponseObject("ok", "Succeeded", subredditService.getAll()));
	}
	
	@GetMapping("/{name}")
	public ResponseEntity<ResponseObject> getSubreddit(@PathVariable String name) {
		return subredditService.getSubreddit(name);
	}
}
