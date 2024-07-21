package admin.bot.adminmoviebot.bot.messengers.send.message.users;

import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public final class SendMessageUsers implements SendMessageUsersInt {

  private final UserService userService;

  public SendMessageUsers(UserService userService) {
    this.userService = userService;
  }

  @Override
  public SendMessage sendTextFormatMessage(Update update) {
    Message message = update.getMessage();
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(message.getText());
    return sendMessage;
  }

  @Override
  public SendPhoto createSendPhotoFormat(Update update) {

    return null;
  }
}
