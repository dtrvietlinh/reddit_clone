package api.service;

import static api.model.VoteType.UPVOTE;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.dto.ResponseObject;
import api.dto.VoteDto;
import api.exception.SpringRedditException;
import api.model.Comment;
import api.model.Point;
import api.repository.CommentRepository;
import api.repository.PointRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PointService {

	private final AuthService authService;
	private final CommentRepository cmtRepository;
	private final PointRepository pointRepository;

	@Transactional
	public ResponseEntity<ResponseObject> vote(VoteDto voteDto) {

		Comment cmt = cmtRepository.findById(voteDto.getId())
				.orElseThrow(() -> new SpringRedditException("Comment not found"));

		Optional<Point> voteByCommentAndUser = pointRepository.findByCommentAndUser(cmt, authService.getCurrentUser());

		if (voteByCommentAndUser.isPresent()) {
			// check if user want to de-vote the comment else re-vote
			if (voteByCommentAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
				pointRepository.delete(voteByCommentAndUser.get());
				;
				if (UPVOTE.equals(voteDto.getVoteType())) {
					cmt.setPoints(cmt.getPoints() - 1);
				} else {
					cmt.setPoints(cmt.getPoints() + 1);
				}
				cmtRepository.save(cmt);
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("deleted", "successfully", ""));
			} else {
				return revote(cmt, voteByCommentAndUser.get(), voteDto);
			}

		}

		cmtRepository.save(UpOrDown(cmt, voteDto, "vote"));
		pointRepository.save(map(voteDto, cmt));

		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject("created", "successfully", ""));
	}
	
	@Transactional
	private ResponseEntity<ResponseObject> revote(Comment cmt, Point point, VoteDto voteDto) {
		point.setVoteType(voteDto.getVoteType());
		cmtRepository.save(UpOrDown(cmt, voteDto, "revote"));
		pointRepository.save(point);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject("created", "successfully", ""));
	}

	private Comment UpOrDown(Comment cmt, VoteDto voteDto, String vote) {
		int step = 1;
		if (vote.equals("revote"))
			step = 2;
		if (UPVOTE.equals(voteDto.getVoteType())) {
			cmt.setPoints(cmt.getPoints() + step);
		} else {
			cmt.setPoints(cmt.getPoints() - step);
		}
		return cmt;
	}

	public Point map(VoteDto voteDto, Comment cmt) {
		return Point.builder()
				.voteType(voteDto.getVoteType())
				.comment(cmt)
				.user(authService.getCurrentUser())
				.build();
	}

}
