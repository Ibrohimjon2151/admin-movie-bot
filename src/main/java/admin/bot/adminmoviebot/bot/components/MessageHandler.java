package admin.bot.adminmoviebot.bot;

import admin.bot.adminmoviebot.bot.components.MainAdminComponent;
import admin.bot.adminmoviebot.bot.components.UsersBotComponent;
import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.category.CategoryMsgSender;
import admin.bot.adminmoviebot.bot.messengers.menu.MenuMessageSender;
import admin.bot.adminmoviebot.bot.messengers.newMovie.NewMovieMsgSender;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import admin.bot.adminmoviebot.dbConfig.service.movie.service.MovieService;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import static admin.bot.adminmoviebot.bot.constants.BotState.*;
import static admin.bot.adminmoviebot.bot.constants.Messages.MSG_ENTER_WANTED_MESSAGE;

@Component
public class MessageHandler {
  private final MovieService movieService;
  private final UserService userService;
  private final MenuMessageSender menuMessageSender;
  private final NewMovieMsgSender newMovieMsgSender;
  private final CategoryMsgSender categoryMsgSender;
  private final UsersBotComponent usersBotComponent;

  public MessageHandler(MovieService movieService, UserService userService,
                        MenuMessageSender menuMessageSender, NewMovieMsgSender newMovieMsgSender,
                        CategoryMsgSender categoryMsgSender, UsersBotComponent usersBotComponent) {
    this.movieService = movieService;
    this.userService = userService;
    this.menuMessageSender = menuMessageSender;
    this.newMovieMsgSender = newMovieMsgSender;
    this.categoryMsgSender = categoryMsgSender;
    this.usersBotComponent = usersBotComponent;
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
        userService.changeUsersStateByUpdate(update, ST_CHOOSE_LANGUAGE);
        break;
      case ST_MOVIE_SIZE:
        userService.changeUsersStateByUpdate(update, ST_MOVIE_YEAR);
        mainAdminComponent.execute(newMovieMsgSender.saveMovieRunTimeSendSize(update));
        break;
      case ST_MOVIE_YEAR:
        userService.changeUsersStateByUpdate(update, ST_MOVIE_CONFIRM);
        mainAdminComponent.execute(newMovieMsgSender.saveSizeSendProductionYear(update));
        break;
      case ST_MOVIE_CONFIRM:
        userService.changeUsersStateByUpdate(update, ST_REVIEW_ADDED_MOVIE);
        mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_CONFIRM_DETAILS_MOVIE));
        mainAdminComponent.execute(newMovieMsgSender.saveMovieYearSendConfirmation(update));
        break;
      case ST_ENTER_NEW_CATEGORY_NAME:
        userService.changeUsersStateByUpdate(update, ST_NEW_CATEGORY);
        categoryMsgSender.saveCategoryName(update);
        mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_SAVED_MOVIE));
        mainAdminComponent.execute(categoryMsgSender.sendAllCategoriesList(update));
        break;
      case ST_SEND_MESSAGE_USERS:
        if (update.getMessage().hasPhoto()) {
          InputFile inputFile = mainAdminComponent.downloadPhoto(update);
          usersBotComponent.sendPhotoFormatMessage(update, inputFile);
        } else if (update.getMessage().hasVideo()) {
          InputFile inputFile = mainAdminComponent.downloadPhoto(update);
          usersBotComponent.sendVideoFormatMessage(update, inputFile);
        } else {
          usersBotComponent.sendTextFormatMessage(update.getMessage().getText());
        }
        mainAdminComponent.execute(TaskUtil.messageSender(update, Messages.MSG_MESSAGE_SENT));
        userService.changeUsersStateByUpdate(update, ST_MENU);
        mainAdminComponent.execute(menuMessageSender.sendMenu(update));
        break;
      default:
        break;
    }
  }

  @SneakyThrows
  private void handleMenuState(String message, Update update, MainAdminComponent mainAdminComponent) {
    switch (message) {
      case Buttons.BTN_ADD_NEW_MOVIE:
        if (movieService.getMoviePostIsNull() != null) {
          Movie moviePostIsNull = movieService.getMoviePostIsNull();
          mainAdminComponent.execute(TaskUtil.messageSender(update, moviePostIsNull.getMovieCode() + Messages.MSG_RESTRICT_ADD_MOVIE));
          userService.changeUsersStateByUpdate(update, ST_MENU);
          mainAdminComponent.execute(menuMessageSender.sendMenu(update));
        } else {
          mainAdminComponent.execute(newMovieMsgSender.sendGeneratedMovieId(update));
          userService.changeUsersStateByUpdate(update, ST_MOVIE_CODE);
        }
        break;
      case Buttons.BTN_SEND_MSG_USERS:
        userService.changeUsersStateByUpdate(update, ST_SEND_MESSAGE_USERS);
        mainAdminComponent.execute(TaskUtil.messageSender(update, MSG_ENTER_WANTED_MESSAGE));
        break;
      case Buttons.BTN_ADD_NEW_CATEGORY:
        userService.changeUsersStateByUpdate(update, ST_NEW_CATEGORY);
        mainAdminComponent.execute(categoryMsgSender.sendAllCategoriesList(update));
        break;
      default:
        break;
    }
  }
}
