package admin.bot.adminmoviebot.bot.messengers.menu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public sealed interface MenuMessageSenderInt permits MenuMessageSender{
  SendMessage sendMenu(Update update);
}
