package api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import api.dto.ResponseObject;
import api.model.Post;
import api.model.User;
import api.model.Vote;
import api.model.VoteType;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>{

	Optional<Vote> findByPostAndUser(Post post, User currentUser);

	List<Vote> findByUserAndVoteType(User currentUser, VoteType vote);
		

}
