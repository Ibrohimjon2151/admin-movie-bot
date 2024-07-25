package admin.bot.adminmoviebot.dbConfig.repository;


import admin.bot.adminmoviebot.dbConfig.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {


  @Query("SELECT c FROM Comment c WHERE c.creationDate >= :fiveDaysAgo")
  List<Comment> findAllCommentsFromLastTenDays(@Param("fiveDaysAgo") Date fiveDaysAgo);
}
