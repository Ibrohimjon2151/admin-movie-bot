package admin.bot.adminmoviebot.bot.component;

import admin.bot.adminmoviebot.bot.MainAdminComponent;
import admin.bot.adminmoviebot.bot.UsersBotComponent;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Getter
public class UsersBotConfig {
 private final String botUsername = "watch_1_movie_bot";
 private final String botToken = "7073355391:AAHXcRWw4Ksg1o-dySCcTtnZqLdmsvlGKXk";

}
