package admin.bot.adminmoviebot.bot.messengers.send.message.users;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

public sealed interface SendMessageUsersInt permits SendMessageUsers {

  SendMessage sendTextFormatMessage(Update update);

  SendPhoto createSendPhotoFormat(Update update);
}
