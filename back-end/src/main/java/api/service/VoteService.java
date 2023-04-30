package api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.dto.PostResponse;
import api.dto.ResponseObject;
import api.dto.VoteDto;
import api.exception.PostNotFoundException;
import api.mapper.PostMapper;
import api.model.Post;
import api.model.Vote;
import api.model.VoteType;
import api.repository.PostRepository;
import api.repository.VoteRepository;
import lombok.AllArgsConstructor;

import static api.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {
	private final VoteRepository voteRepository;
	private final PostRepository postRepository;
	private final AuthService authService;
	private final PostMapper postMapper;

	@Transactional
	public ResponseEntity<ResponseObject> vote(VoteDto voteDto) {
		Post post = postRepository.findById(voteDto.getId())
				.orElseThrow(() -> new PostNotFoundException("Can't find post with id " + voteDto.getId()));
		Optional<Vote> voteByPostAndUser = voteRepository.findByPostAndUser(post, authService.getCurrentUser());

		if (voteByPostAndUser.isPresent()) {
			// check if user want to de-vote the post else re-vote
			if (voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
				voteRepository.delete(voteByPostAndUser.get());
				if (UPVOTE.equals(voteDto.getVoteType())) {
					post.setVoteCount(post.getVoteCount() - 1);
				} else {
					post.setVoteCount(post.getVoteCount() + 1);
				}
				postRepository.save(post);
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("deleted", "successfully", ""));
			} else {
				return revote(post, voteByPostAndUser.get(), voteDto);
			}

		}

		postRepository.save(UpOrDown(post, voteDto, "vote"));
		voteRepository.save(map(voteDto, post));

		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject("created", "successfully", ""));
	}

	@Transactional
	private ResponseEntity<ResponseObject> revote(Post post, Vote vote, VoteDto voteDto) {
		vote.setVoteType(voteDto.getVoteType());
		postRepository.save(UpOrDown(post, voteDto, "revote"));
		voteRepository.save(vote);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject("created", "successfully", ""));
	}

	private Post UpOrDown(Post post, VoteDto voteDto, String vote) {
		int step = 1;
		if (vote.equals("revote"))
			step = 2;
		if (UPVOTE.equals(voteDto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() + step);
		} else {
			post.setVoteCount(post.getVoteCount() - step);
		}
		return post;
	}

	public Vote map(VoteDto voteDto, Post post) {
		return Vote.builder()
				.voteType(voteDto.getVoteType())
				.post(post)
				.user(authService.getCurrentUser())
				.build();
	}

	@Transactional
	public List<PostResponse> getVotedPosts(int direction) {
		return voteRepository.findByUserAndVoteType(authService.getCurrentUser(), VoteType.lookup(direction)).stream()
				.map(vote -> vote.getPost()).map(postMapper::mapToDto).toList();
	}
}
