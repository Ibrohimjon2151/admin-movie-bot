package admin.bot.adminmoviebot.bot.messengers.menu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public sealed interface MenuMessageSenderInt permits MenuMessageSender{
  SendMessage sendMenu(Update update);

  DeleteMessage deleteMessage(Update update);

  List<DeleteMessage> deleteMessagesAllAdmins(Update update);
}
