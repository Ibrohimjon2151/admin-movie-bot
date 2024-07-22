package admin.bot.adminmoviebot.bot.components;

import admin.bot.adminmoviebot.bot.messengers.channel.ChannelMessengers;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ChannelMessageHandler {

  private final ChannelMessengers channelMessengers;

  public ChannelMessageHandler(ChannelMessengers channelMessengers) {
    this.channelMessengers = channelMessengers;
  }


  public void handlerMessage(Update update, MainAdminComponent mainAdminComponent) throws TelegramApiException {
    if (channelMessengers.checkMessageShareable(update)) {

      for (ForwardMessage forwardMessageToAdmin : channelMessengers.forwardMessageToAdmins(update)) {
        mainAdminComponent.execute(forwardMessageToAdmin);
      }
      for (SendMessage sendMessage : channelMessengers.handlePostFromChannel(update)) {
        mainAdminComponent.execute(sendMessage);
      }
    }
  }


}
