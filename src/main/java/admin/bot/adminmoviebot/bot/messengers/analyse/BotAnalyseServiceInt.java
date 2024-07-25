package admin.bot.adminmoviebot.bot.messengers.analyse;

import admin.bot.adminmoviebot.dbConfig.entity.Comment;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.Serializable;

public sealed interface BotAnalyseServiceInt permits BotAnalyseService {

  SendMessage sendAnalyseDetailButton(Update update);

  Object responseBotAnalyseOptions(Update update);

  SendMessage drawCommentMessage(Update update, Comment comment);

  SendMessage responseUsersComment(Update update);

  void sendResponseUsersComment(Update update) throws TelegramApiException, IOException;

  SendMessage onHandleReply(Update update) throws TelegramApiException, IOException;
}
