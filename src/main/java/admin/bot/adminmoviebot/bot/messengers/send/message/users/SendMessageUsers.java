package admin.bot.adminmoviebot.bot.messengers.send.message.users;

import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

@Service
public final class SendMessageUsers implements SendMessageUsersInt {

  private final UserService userService;

  public SendMessageUsers(UserService userService) {
    this.userService = userService;
  }

  @Override
  public SendMessage sendTextFormatMessage(Update update) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(Messages.MSG_ENTER_WANTED_MESSAGE);
    String [] buttons = {Buttons.BTN_BACK};
    ReplyKeyboardMarkup twoColumnKeyboard = TaskUtil.createTwoColumnKeyboard(buttons);
    sendMessage.setReplyMarkup(twoColumnKeyboard);
    return sendMessage;
  }

}
