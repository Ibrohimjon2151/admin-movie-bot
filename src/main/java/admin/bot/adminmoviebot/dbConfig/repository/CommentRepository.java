package admin.bot.adminmoviebot.dbConfig.repository;


import admin.bot.adminmoviebot.dbConfig.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
