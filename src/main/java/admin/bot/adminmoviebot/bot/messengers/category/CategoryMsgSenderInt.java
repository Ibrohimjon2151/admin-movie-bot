package admin.bot.adminmoviebot.bot.messengers.category;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

public sealed interface CategoryMsgSenderInt permits CategoryMsgSender {
  SendMessage sendAllCategoriesList(Update update);

  SendMessage deleteOrAddCategory(Update update);

  SendMessage saveCategoryLangSendName(Update update);

  void saveCategoryName(Update update);

  SendMessage chooseLanguageCode(Update update);
}
