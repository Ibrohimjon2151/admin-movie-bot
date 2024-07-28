package admin.bot.adminmoviebot.bot.components;

import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.admin.AdminSendMessenger;
import admin.bot.adminmoviebot.bot.messengers.analyse.BotAnalyseService;
import admin.bot.adminmoviebot.bot.messengers.category.CategoryMsgSender;
import admin.bot.adminmoviebot.bot.messengers.channel.ChannelMessengers;
import admin.bot.adminmoviebot.bot.messengers.menu.MenuMessageSender;
import admin.bot.adminmoviebot.bot.messengers.newMovie.NewMovieMsgSender;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static admin.bot.adminmoviebot.bot.constants.BotState.*;

@Component
public class CallbackHandler {
 private final UserService userService;
 private final MenuMessageSender menuMessageSender;
 private final NewMovieMsgSender newMovieMsgSender;
 private final CategoryMsgSender categoryMsgSender;
 private final ChannelMessengers channelMessengers;
 private final UsersBotComponent usersBotComponent;
 private final BotAnalyseService botAnalyseService;
 private final AdminSendMessenger adminSendMessenger;

 public CallbackHandler(UserService userService, MenuMessageSender menuMessageSender,
                        NewMovieMsgSender newMovieMsgSender, CategoryMsgSender categoryMsgSender,
                        ChannelMessengers channelMessengers, UsersBotComponent usersBotComponent, BotAnalyseService botAnalyseService, AdminSendMessenger adminSendMessenger) {
  this.userService = userService;
  this.menuMessageSender = menuMessageSender;
  this.newMovieMsgSender = newMovieMsgSender;
  this.categoryMsgSender = categoryMsgSender;
  this.channelMessengers = channelMessengers;
  this.usersBotComponent = usersBotComponent;
  this.botAnalyseService = botAnalyseService;
  this.adminSendMessenger = adminSendMessenger;
 }

 @SneakyThrows
 public void handleCallback(Update update, MainAdminComponent mainAdminComponent) {
  Long chatId = TaskUtil.getChatId(update);
  BotState userState = userService.getUsersStateByChatId(chatId);
  BotState previousState = userService.getUserPreviousState(chatId);
  String data = update.getCallbackQuery().getData();

  switch (userState) {
   case ST_MOVIE_CODE:
    userService.setUsersStateByUpdate(update, ST_ENTER_MOVIE_NAME);
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    mainAdminComponent.execute(newMovieMsgSender.copyCodeSendName(update));
    break;
   case ST_CHOOSE_LANGUAGE:
    userService.setUsersStateByUpdate(update, ST_MOVIE_QUALITY);
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    mainAdminComponent.execute(newMovieMsgSender.saveCategoryChooseLang(update));
    break;
   case ST_MOVIE_QUALITY:
    userService.setUsersStateByUpdate(update, ST_MOVIE_RUNTIME);
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    mainAdminComponent.execute(newMovieMsgSender.saveLangChooseQuality(update));
    break;
   case ST_MOVIE_RUNTIME:
    userService.setUsersStateByUpdate(update, ST_MOVIE_SIZE);
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    mainAdminComponent.execute(newMovieMsgSender.saveQualitySendRunTime(update));
    break;
   case ST_REVIEW_ADDED_MOVIE:
    handleReviewAddedMovie(data, update, mainAdminComponent);
    break;
   case ST_MOVIE_CONFIRM:
    userService.setUsersStateByUpdate(update, ST_REVIEW_ADDED_MOVIE);
    mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_CONFIRM_DETAILS_MOVIE));
    mainAdminComponent.execute(newMovieMsgSender.saveMovieYearSendConfirmation(update));
    break;
   case ST_EDIT_MOVIE:
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    mainAdminComponent.execute(newMovieMsgSender.editExactParameter(update));
    break;
   case ST_NEW_CATEGORY:
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    mainAdminComponent.execute(categoryMsgSender.deleteOrAddCategory(update));
    break;
   case ST_DO_FORWARD:
    handleDoForward(data, update, mainAdminComponent);
    break;
   case ST_CONNECT_TRAILER:
    handleConnectTrailer(data, update, mainAdminComponent);
    break;
   case ST_CONNECT_ORIGINAL_MOVIE:
    handleConnectOriginalMovie(data, update, mainAdminComponent);
    break;
   case ST_ENTER_NEW_CATEGORY_LANG:
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    mainAdminComponent.execute(categoryMsgSender.saveCategoryLangSendName(update));
    break;
   case ST_MOVIE_CATEGORY_TO_GET_LIST:
    List<SendMessage> allMoviesByCategory = newMovieMsgSender.drawMovieByCategory(update);
    for (SendMessage sendMessage : allMoviesByCategory) {
     mainAdminComponent.execute(sendMessage);
    }
    break;
   case ST_ANALYSE_OPTIONS:
    mainAdminComponent.execute(botAnalyseService.responseUsersComment(update));
    userService.setUsersStateByUpdate(update, BotState.ST_RESPONSE_COMMENT);
    break;
   case ST_HANDLE_SELECTED_FIELD:
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    if (update.getCallbackQuery().getData().equals(Buttons.BTN_BACK)) {
          mainAdminComponent.execute(botAnalyseService.sendAnalyseDetailButton(update));
    } else {
     mainAdminComponent.execute(adminSendMessenger.sendFieldsMessage(update));
    }
    break;
   default:
    break;
  }
  if (previousState.equals(ST_DELETE_OR_ROUTE) && userState.equals(ST_MOVIE_CATEGORY_TO_GET_LIST)) {
   DeleteMessage deleteMessage = newMovieMsgSender.deleteOrRouteTrailer(update);
   if (deleteMessage.getChatId() != null) {
    mainAdminComponent.execute(deleteMessage);
   }
  }
 }

 private void handleConnectOriginalMovie(String data, Update update, MainAdminComponent mainAdminComponent) throws TelegramApiException {
  switch (data) {
   case Buttons.DATA_YES_MESSAGE:
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    channelMessengers.connectMovieAndPost();
    mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_CONNECTED_SUCCESSFULLY));
    break;
   case Buttons.DATA_NO_MESSAGE:
    for (DeleteMessage deleteMessagesAllAdmin : menuMessageSender.deleteMessagesAllAdmins(update)) {
     mainAdminComponent.execute(deleteMessagesAllAdmin);
    }
    break;
   default:
    break;
  }
 }

 @SneakyThrows
 private void handleReviewAddedMovie(String data, Update update, MainAdminComponent mainAdminComponent) {
  switch (data) {
   case Buttons.BTN_YES:
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_SAVED_MOVIE));
    mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_UPLOAD_MOVIE_TO_CONNECT));
    mainAdminComponent.execute(menuMessageSender.sendMenu(update));
    userService.setUsersStateByUpdate(update, ST_MENU);
    break;
   case Buttons.BTN_NO:
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_MOVIE_DETAILS_DELETED));
    mainAdminComponent.execute(menuMessageSender.sendMenu(update));
    userService.setUsersStateByUpdate(update, ST_MENU);
    break;
   case Buttons.BTN_EDIT:
    userService.setUsersStateByUpdate(update, ST_EDIT_MOVIE);
    mainAdminComponent.execute(newMovieMsgSender.sendEditParameters(update));
    break;
  }
 }

 @SneakyThrows
 private void handleDoForward(String data, Update update, MainAdminComponent mainAdminComponent) {
  switch (data) {
   case Buttons.DATA_YES_MESSAGE:
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    usersBotComponent.forwardMessageToUsers(channelMessengers.forwardSelectedMessageToUsers());
    mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_MESSAGE_SENT));
    break;
   case Buttons.DATA_NO_MESSAGE:
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    break;
  }
  for (Long l : userService.getAdminsUserId()) {
   userService.setUsersStateByChatId(l, ST_MENU);
  }
 }

 @SneakyThrows
 private void handleConnectTrailer(String data, Update update, MainAdminComponent mainAdminComponent) {
  switch (data) {
   case Buttons.DATA_YES_MESSAGE:
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    channelMessengers.connectMovieAndPost();
    mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_CONNECTED_SUCCESSFULLY));
    break;
   case Buttons.DATA_NO_MESSAGE:
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    break;
   case Buttons.BTN_FORWARD_BOT_USERS:
    mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
    usersBotComponent.forwardMessageToUsers(channelMessengers.forwardSelectedMessageToUsers());
    mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_MESSAGE_SENT));
    break;
  }
  for (Long l : userService.getAdminsUserId()) {
   userService.setUsersStateByChatId(l, ST_MENU);
  }
 }
}
