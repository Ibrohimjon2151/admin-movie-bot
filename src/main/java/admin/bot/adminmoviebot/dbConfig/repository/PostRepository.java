package admin.bot.adminmoviebot.dbConfig.repository;

import admin.bot.adminmoviebot.dbConfig.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

 @Query("SELECT p FROM Post p WHERE p.originalMovie = false ORDER BY p.lastUpdatedDate DESC limit 1")
 Post getLastUpdated();
}
