package admin.bot.adminmoviebot.dbConfig.payload;

import admin.bot.adminmoviebot.bot.constants.BotState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
  private Long chatId;

  private BotState botState;

  private int lang;

  private String fullName;

  private String userName;

}
