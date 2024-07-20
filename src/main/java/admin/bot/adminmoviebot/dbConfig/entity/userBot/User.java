package admin.bot.adminmoviebot.dbConfig.entity.userBot;

import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.dbConfig.entity.template.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class User extends AbstractEntity {

  @Column(name = "chat_id", nullable = false, unique = true)
  private Long chatId;

  @Enumerated(EnumType.STRING)
  @Column(name = "bot_state", nullable = false)
  private BotState botState;

  @Enumerated(EnumType.STRING)
  private BotState previousState;

  @Column(name = "lang", nullable = false)
  private int lang;

  private String fullName;

  private String userName;

  private String movieCode;

  private boolean admin;

}
