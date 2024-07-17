package admin.bot.adminmoviebot.bot.component;

import admin.bot.adminmoviebot.bot.MainAdminComponent;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Getter
public class BotConfigComponent {

  private final String botUsername = "main_1_movie_bot";
  private final String botToken = "7477750373:AAFfsHUJgSmKmcTdQgvmwEXvLb0JfXMkROQ";

  @Bean
  public TelegramBotsApi telegramBotsApi(MainAdminComponent mainAdminComponent) throws TelegramApiException {
    TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
    telegramBotsApi.registerBot(mainAdminComponent);
    return telegramBotsApi;
  }
}
