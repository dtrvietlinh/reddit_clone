package api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.model.Comment;
import api.model.Point;
import api.model.User;

@Repository
public interface PointRepository extends JpaRepository<Point, Long>{

	Optional<Point> findByCommentAndUser(Comment cmt, User currentUser);

}
