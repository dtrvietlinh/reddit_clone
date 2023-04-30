package api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.model.Post;
import api.model.Subreddit;
import api.model.User;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	
	List<Post> findBySubreddit(Subreddit subreddit);

	List<Post> findAllByUser(User user);

}
