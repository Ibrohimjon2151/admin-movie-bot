package admin.bot.adminmoviebot.dbConfig.repository;


import admin.bot.adminmoviebot.bot.constants.Language;
import admin.bot.adminmoviebot.dbConfig.entity.user.bot.User;
import admin.bot.adminmoviebot.dbConfig.payload.LanguageCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByChatId(Long chatId);

  @Query("SELECT u.chatId FROM users u where u.admin = true ")
  List<Long> findAllChatIds();

  @Query("select t.creationDate from users t where t.creationDate = (select min(a.creationDate) from users a )")
  Date findCreationDate();


  @Query("SELECT u.lang, count(*) FROM users u where u.creationDate between :begin and :end group by u.lang")
  List<Object[]> findUsersCountBetweenTwoDates(Date begin, Date end);

  @Query("SELECT count(*) FROM users u where u.creationDate <= :endDate ")
  int getUsersCountUntilDate(Date endDate);

  @Query("SELECT u.lang, count(*) FROM users u where u.creationDate <= :endDate  group by u.lang")
  List<Object[]> getUsersCountLangCountUntilDate(Date endDate);

  @Query("SELECT count(*) FROM users u where u.creationDate between :beginDate and :endDate")
  int getUsersCountBetweenDates(Date beginDate, Date endDate);
}
