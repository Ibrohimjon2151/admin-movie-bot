package admin.bot.adminmoviebot.dbConfig.repository;


import admin.bot.adminmoviebot.dbConfig.entity.user.bot.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
  Optional<User> findByChatId(Long chatId);

  @Query("SELECT u.chatId FROM users u where u.admin = true ")
  List<Long> findAllChatIds();
}
