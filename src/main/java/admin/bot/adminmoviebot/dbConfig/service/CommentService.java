package admin.bot.adminmoviebot.dbConfig.service;

import admin.bot.adminmoviebot.dbConfig.entity.Comment;
import admin.bot.adminmoviebot.dbConfig.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CommentService {


  private final CommentRepository commentRepository;

  public CommentService(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  //GET ALL COMMENTS COME FROM USERS WITHIN FIVE DAYS
  public List<Comment> getLastTenDaysComments() {
    int daysPeriod = 10;
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -daysPeriod);
    Date fiveDaysAgo = calendar.getTime();
    return commentRepository.findAllCommentsFromLastTenDays(fiveDaysAgo);
  }
}
