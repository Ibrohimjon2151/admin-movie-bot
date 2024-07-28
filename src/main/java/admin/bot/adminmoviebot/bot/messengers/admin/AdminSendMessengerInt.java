package admin.bot.adminmoviebot.bot.messengers.admin;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

public sealed interface AdminSendMessengerInt permits AdminSendMessenger{
 SendMessage sendAdminDetails(Update update);

 SendMessage sendFieldsMessage(Update update);

 SendMessage handleNewAdminField(Update update);

}
