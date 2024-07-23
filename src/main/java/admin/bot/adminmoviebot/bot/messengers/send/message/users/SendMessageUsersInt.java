package admin.bot.adminmoviebot.bot.messengers.send.message.users;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public sealed interface SendMessageUsersInt permits SendMessageUsers {

  SendMessage sendTextFormatMessage(Update update);

}
