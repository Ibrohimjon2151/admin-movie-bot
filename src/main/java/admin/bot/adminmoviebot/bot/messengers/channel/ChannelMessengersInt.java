package admin.bot.adminmoviebot.bot.messengers.channel;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public sealed interface ChannelMessengersInt permits ChannelMessengers {
 List<SendMessage> handlePostFromChannel(Update update);

 List<ForwardMessage> forwardMessageToAdmins(Update update);

 List<ForwardMessage> forwardSelectedMessageToUsers();
}
