package admin.bot.adminmoviebot.bot.components;

import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.analyse.BotAnalyseService;
import admin.bot.adminmoviebot.bot.messengers.category.CategoryMsgSender;
import admin.bot.adminmoviebot.bot.messengers.menu.MenuMessageSender;
import admin.bot.adminmoviebot.bot.messengers.newMovie.NewMovieMsgSender;
import admin.bot.adminmoviebot.bot.messengers.send.message.users.SendMessageUsers;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.service.movie.service.MovieService;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static admin.bot.adminmoviebot.bot.constants.BotState.*;
import static admin.bot.adminmoviebot.bot.constants.Messages.MSG_MESSAGE_SENT;

@Component
public class MessageHandler {
  private final MovieService movieService;
  private final UserService userService;
  private final MenuMessageSender menuMessageSender;
  private final NewMovieMsgSender newMovieMsgSender;
  private final CategoryMsgSender categoryMsgSender;
  private final UsersBotComponent usersBotComponent;
  private final SendMessageUsers sendMessageUsers;
  private final BotAnalyseService botAnalyseService;

  public MessageHandler(MovieService movieService, UserService userService,
                        MenuMessageSender menuMessageSender, NewMovieMsgSender newMovieMsgSender,
                        CategoryMsgSender categoryMsgSender, UsersBotComponent usersBotComponent, SendMessageUsers sendMessageUsers, BotAnalyseService botAnalyseService) {
    this.movieService = movieService;
    this.userService = userService;
    this.menuMessageSender = menuMessageSender;
    this.newMovieMsgSender = newMovieMsgSender;
    this.categoryMsgSender = categoryMsgSender;
    this.usersBotComponent = usersBotComponent;
    this.sendMessageUsers = sendMessageUsers;
    this.botAnalyseService = botAnalyseService;
  }

  @SneakyThrows
  public void handleMessage(Update update, MainAdminComponent mainAdminComponent) {
    Long chatId = TaskUtil.getChatId(update);
    BotState userState = userService.getUsersStateByChatId(chatId);
    String message = update.getMessage().getText();

    switch (userState) {
      case ST_START:
        mainAdminComponent.execute(menuMessageSender.sendMenu(update));
        break;
      case ST_MENU:
        handleMenuState(message, update, mainAdminComponent);
        break;
      case ST_ENTER_MOVIE_NAME:
        mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
        mainAdminComponent.execute(newMovieMsgSender.saveNameChooseCategory(update));
        userService.setUsersStateByUpdate(update, ST_CHOOSE_LANGUAGE);
        break;
      case ST_MOVIE_SIZE:
        userService.setUsersStateByUpdate(update, ST_MOVIE_YEAR);
        mainAdminComponent.execute(newMovieMsgSender.saveMovieRunTimeSendSize(update));
        break;
      case ST_MOVIE_YEAR:
        userService.setUsersStateByUpdate(update, ST_MOVIE_CONFIRM);
        mainAdminComponent.execute(newMovieMsgSender.saveSizeSendProductionYear(update));
        break;
      case ST_MOVIE_CONFIRM:
        userService.setUsersStateByUpdate(update, ST_REVIEW_ADDED_MOVIE);
        mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_CONFIRM_DETAILS_MOVIE));
        mainAdminComponent.execute(newMovieMsgSender.saveMovieYearSendConfirmation(update));
        break;
      case ST_ENTER_NEW_CATEGORY_NAME:
        userService.setUsersStateByUpdate(update, ST_NEW_CATEGORY);
        categoryMsgSender.saveCategoryName(update);
        mainAdminComponent.execute(menuMessageSender.deleteMessage(update));
        mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_SAVED_MOVIE));
        mainAdminComponent.execute(categoryMsgSender.sendAllCategoriesList(update));
        break;
      case ST_SEND_MESSAGE_USERS:
        if (update.getMessage().hasPhoto()) {
          InputFile inputFile = mainAdminComponent.downloadPhoto(update);
          usersBotComponent.sendPhotoFormatMessage(update, inputFile, null);
          mainAdminComponent.execute(TaskUtil.messageSender(update, MSG_MESSAGE_SENT));
        } else if (update.getMessage().hasVideo()) {
          InputFile inputFile = mainAdminComponent.downloadPhoto(update);
          usersBotComponent.sendVideoFormatMessage(update, inputFile, null);
          mainAdminComponent.execute(TaskUtil.messageSender(update, MSG_MESSAGE_SENT));
        } else {
          if (!update.getMessage().getText().equals(Buttons.BTN_BACK)) {
            usersBotComponent.sendTextFormatMessage(update.getMessage().getText(), null,null);
            mainAdminComponent.execute(TaskUtil.messageSender(update, MSG_MESSAGE_SENT));
          }
        }
        userService.setUsersStateByUpdate(update, ST_MENU);
        mainAdminComponent.execute(menuMessageSender.sendMenu(update));
        break;
      case ST_MOVIE_OPTIONS:
        mainAdminComponent.execute(newMovieMsgSender.movieFunctions(update));
        break;
      case ST_MOVIE_CODE:
        mainAdminComponent.execute(newMovieMsgSender.sendGeneratedMovieId(update));
        break;
      case ST_ANALYSE_OPTIONS:
        Object object = botAnalyseService.responseBotAnalyseOptions(update);
        if (object instanceof SendMessage) {
          mainAdminComponent.execute((SendMessage) object);
        } else {
          List<SendMessage> forwardMessages = (List<SendMessage>) object;
          forwardMessages.forEach(forwardMessage -> {
            try {
              mainAdminComponent.execute(forwardMessage);
            } catch (TelegramApiException e) {
              throw new RuntimeException(e);
            }
          });
        }
        break;
      case ST_RESPONSE_COMMENT:
        mainAdminComponent.execute(botAnalyseService.onHandleReply(update));
        userService.setUsersStateByUpdate(update, ST_ANALYSE_OPTIONS);
        break;
    }
  }

  @SneakyThrows
  private void handleMenuState(String message, Update update, MainAdminComponent mainAdminComponent) {
    switch (message) {
      case Buttons.BTN_MOVIES:
        mainAdminComponent.execute(newMovieMsgSender.sendMovieDetails(update, null));
        break;
      case Buttons.BTN_SEND_MSG_USERS:
        userService.setUsersStateByUpdate(update, ST_SEND_MESSAGE_USERS);
        mainAdminComponent.execute(sendMessageUsers.sendTextFormatMessage(update));
        break;
      case Buttons.BTN_ADD_NEW_CATEGORY:
        userService.setUsersStateByUpdate(update, ST_NEW_CATEGORY);
        mainAdminComponent.execute(categoryMsgSender.sendAllCategoriesList(update));
        break;
      case Buttons.BTN_ANALYSE:
        mainAdminComponent.execute(botAnalyseService.sendAnalyseDetailButton(update));
        break;
      default:
        break;
    }
  }

}
