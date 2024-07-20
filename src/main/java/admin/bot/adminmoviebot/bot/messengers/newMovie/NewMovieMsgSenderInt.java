package admin.bot.adminmoviebot.bot.messengers.newMovie;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

public sealed interface NewMovieMsgSenderInt permits NewMovieMsgSender{
  SendMessage sendGeneratedMovieId(Update update);

  SendMessage copyCodeSendName(Update update);

  SendMessage saveNameChooseCategory(Update update);

  SendMessage saveCategoryChooseLang(Update update);

  SendMessage saveLangChooseQuality(Update update);

  SendMessage saveQualitySendRunTime(Update update);

  SendMessage saveMovieRunTimeSendSize(Update update);

  SendMessage saveSizeSendProductionYear(Update update);

  SendMessage saveMovieYearSendConfirmation(Update update);

  EditMessageText sendEditParameters(Update update);

  SendMessage editExactParameter(Update update);
}
