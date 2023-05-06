package api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.dto.ResponseObject;
import api.dto.SubredditDto;
import api.exception.SpringRedditException;
import api.mapper.SubredditMapper;
import api.model.Subreddit;
import api.repository.SubredditRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubredditService {
	
	private final SubredditRepository subredditRepository;
	private final SubredditMapper subredditMapper;
	private final AuthService authService;
	
	@Transactional
	public SubredditDto save(SubredditDto subredditDto) {
		if(subredditDto != null) {
			Subreddit save = subredditRepository.save(
					subredditMapper.map(subredditDto, authService.getCurrentUser()));
			subredditDto.setId(save.getId());
		}
		
		return subredditDto;
	}
	
	@Transactional(readOnly = true)
	public List<SubredditDto> getAll() {
		return subredditRepository.findAll().stream()
				.map(subredditMapper::mapToDto)
				.toList();
	}

	@Transactional
	public ResponseEntity<ResponseObject> getSubreddit(String name) {
		Subreddit subreddit = subredditRepository.findByName(name)
								.orElseThrow(() -> new SpringRedditException("Subreddit not found"));
		return ResponseEntity.ok()
				.body(new ResponseObject("ok", "Subreddit found", subredditMapper.mapToDto(subreddit)));
	}
	
	
}
