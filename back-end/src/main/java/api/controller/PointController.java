package api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.dto.ResponseObject;
import api.dto.VoteDto;
import api.service.PointService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/points")
@AllArgsConstructor
public class PointController {
	private final PointService pointService;
	
	@PostMapping
	public ResponseEntity<ResponseObject> vote(@RequestBody VoteDto voteDto) {
		return pointService.vote(voteDto);
	}
}
