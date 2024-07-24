package admin.bot.adminmoviebot.bot.messengers.menu;

import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.payload.UserDto;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

import static admin.bot.adminmoviebot.bot.constants.Buttons.*;

@Service
public final class MenuMessageSender implements MenuMessageSenderInt {

 private final UserService userService;

 public MenuMessageSender(UserService userService) {
  this.userService = userService;
 }

 @Override
 public SendMessage sendMenu(Update update) {
  SendMessage sendMessage = new SendMessage();
  sendMessage.setText(Messages.MSG_MENU);
  Long chatId = TaskUtil.getChatId(update);
  boolean isExist = userService.isExistsByChatId(chatId);
  if (!isExist) {
   UserDto user = new UserDto();
   user.setChatId(chatId);
   user.setLang(0);
   user.setBotState(BotState.ST_MENU);
   userService.saveUser(user);
  }
  sendMessage.setChatId(TaskUtil.getChatIdStr(update));

  String[] menus = {BTN_ANALYSE, BTN_SEND_MSG_USERS, BTN_MOVIES, BTN_ADD_NEW_CATEGORY};
  ReplyKeyboardMarkup keyboardMarkup = TaskUtil.createTwoColumnKeyboard(menus);
  sendMessage.setReplyMarkup(keyboardMarkup);

  return sendMessage;
 }

 @Override
 public DeleteMessage deleteMessage(Update update) {
  DeleteMessage deleteMessage = new DeleteMessage();
  long chatId = 0l;
  int messageId = 0;
  if (update.hasMessage()) {
   chatId = update.getMessage().getChatId();
   messageId = update.getMessage().getMessageId();
  } else {
   chatId = update.getCallbackQuery().getMessage().getChatId();
   messageId = update.getCallbackQuery().getMessage().getMessageId();
  }
  deleteMessage.setMessageId(messageId);
  deleteMessage.setChatId(String.valueOf(chatId));
  return deleteMessage;
 }

 @Override
 public List<DeleteMessage> deleteMessagesAllAdmins(Update update) {
  List<DeleteMessage> deleteMessages = new ArrayList<>();
  DeleteMessage deleteMessage = new DeleteMessage();
  int messageId = 0;
  if (update.hasMessage()) {
   messageId = update.getMessage().getMessageId();
  } else {
   messageId = update.getCallbackQuery().getMessage().getMessageId();
  }
  for (Long l : userService.getAdminsUserId()) {
   deleteMessage.setMessageId(messageId);
   deleteMessage.setChatId(String.valueOf(l));
  deleteMessages.add(deleteMessage);
  }
  return deleteMessages;
 }
}
