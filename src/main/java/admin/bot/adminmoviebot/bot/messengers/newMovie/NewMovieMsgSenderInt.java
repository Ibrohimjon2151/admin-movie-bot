package admin.bot.adminmoviebot.bot.messengers.newMovie;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

public sealed interface NewMovieMsgSenderInt permits NewMovieMsgSender{
  SendMessage sendGeneratedMovieId(Update update);

  EditMessageText copyCodeSendName(Update update);
}
