package admin.bot.adminmoviebot.bot;

import admin.bot.adminmoviebot.bot.component.BotConfigComponent;
import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.menu.MenuMessageSender;
import admin.bot.adminmoviebot.bot.messengers.newMovie.NewMovieMsgSender;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.service.UserService;
import jakarta.persistence.Cache;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static admin.bot.adminmoviebot.bot.constants.BotState.*;
import static admin.bot.adminmoviebot.bot.constants.Messages.MSG_START;

@Component
public class MainAdminComponent extends TelegramLongPollingBot {

  private final BotConfigComponent botConfigComponent;
  private final MenuMessageSender menuMessageSender;
  private final UserService userService;
  private final NewMovieMsgSender newMovieMsgSender;

  public MainAdminComponent(BotConfigComponent botConfigComponent, MenuMessageSender menuMessageSender, UserService userService, NewMovieMsgSender newMovieMsgSender) {
    this.botConfigComponent = botConfigComponent;
    this.menuMessageSender = menuMessageSender;
    this.userService = userService;
    this.newMovieMsgSender = newMovieMsgSender;
  }


  String message = "";
  String data = "";
  BotState userState;

  @SneakyThrows
  @Override
  public void onUpdateReceived(Update update) {
    userState = userService.getUsersStateByChatId(TaskUtil.getChatId(update));
    if (update.hasMessage()) {
      message = update.getMessage().getText();
      if (message.equals(MSG_START)) {
        execute(menuMessageSender.sendMenu(update));
        userService.changeUsersStateByChatId(update, ST_MENU);
      } else {
        switch (userState) {
          case ST_START -> {
            execute(menuMessageSender.sendMenu(update));
          }
          case ST_MENU -> {
            switch (message) {
              case Buttons.BTN_ANALYSE -> {
                // GET INFORMATION ABOUT BOT
              }
              case Buttons.BTN_ADD_NEW_MOVIE -> {
                execute(newMovieMsgSender.sendGeneratedMovieId(update));
                userService.changeUsersStateByChatId(update, ST_MOVIE_CODE);
              }
              case Buttons.BTN_SEND_MSG_USERS -> {
                // METHODS FOR SENDING MESSAGE TO USERS
              }
            }
          }
          case ST_ENTER_MOVIE_NAME -> {
            execute(menuMessageSender.deleteMessage(update));
            execute(newMovieMsgSender.saveNameChooseCategory(update));
            userService.changeUsersStateByChatId(update, ST_CHOOSE_LANGUAGE);
          }
          case ST_MOVIE_SIZE -> {
            userService.changeUsersStateByChatId(update, ST_MOVIE_YEAR);
            execute(newMovieMsgSender.saveMovieRunTimeSendSize(update));
          }
          case ST_MOVIE_YEAR -> {
            userService.changeUsersStateByChatId(update, ST_MOVIE_CONFIRM);
            execute(newMovieMsgSender.saveSizeSendProductionYear(update));
          }
          case ST_MOVIE_CONFIRM -> {
            userService.changeUsersStateByChatId(update, ST_REVIEW_ADDED_MOVIE);
            execute(TaskUtil.messageSender(update, Messages.MSG_CONFIRM_DETAILS_MOVIE));
            execute(newMovieMsgSender.saveMovieYearSendConfirmation(update));
          }
        }
      }
    } else if (update.hasCallbackQuery()) {
      String data = update.getCallbackQuery().getData();
      switch (userState) {
        case ST_MOVIE_CODE -> {
          userService.changeUsersStateByChatId(update, ST_ENTER_MOVIE_NAME);
          execute(menuMessageSender.deleteMessage(update));
          execute(newMovieMsgSender.copyCodeSendName(update));
        }
        case ST_CHOOSE_LANGUAGE -> {
          userService.changeUsersStateByChatId(update, ST_MOVIE_QUALITY);
          execute(newMovieMsgSender.saveCategoryChooseLang(update));
        }
        case ST_MOVIE_QUALITY -> {
          userService.changeUsersStateByChatId(update, ST_MOVIE_RUNTIME);
          execute(newMovieMsgSender.saveLangChooseQuality(update));
        }
        case ST_MOVIE_RUNTIME -> {
          userService.changeUsersStateByChatId(update, ST_MOVIE_SIZE);
          execute(menuMessageSender.deleteMessage(update));
          execute(newMovieMsgSender.saveQualitySendRunTime(update));
        }
        case ST_REVIEW_ADDED_MOVIE -> {
          switch (data) {
            case Buttons.BTN_YES -> {
              execute(menuMessageSender.deleteMessage(update));
              execute(TaskUtil.messageSender(update, Messages.MSG_SAVED_MOVIE));
              execute(TaskUtil.messageSender(update, Messages.MSG_UPLOAD_MOVIE_TO_CONNECT));
              execute(menuMessageSender.sendMenu(update));
              userService.changeUsersStateByChatId(update, ST_MENU);
            }
            case Buttons.BTN_NO -> {
              execute(menuMessageSender.deleteMessage(update));
              execute(TaskUtil.messageSender(update, Messages.MSG_MOVIE_DETAILS_DELETED));
              execute(menuMessageSender.sendMenu(update));
              userService.changeUsersStateByChatId(update, ST_MENU);
            }
            case Buttons.BTN_EDIT -> {
              userService.changeUsersStateByChatId(update, ST_EDIT_MOVIE);
              execute(newMovieMsgSender.sendEditParameters(update));
            }
          }
        }
        case ST_EDIT_MOVIE -> {
          execute(menuMessageSender.deleteMessage(update));
          execute(newMovieMsgSender.editExactParameter(update));
        }
      }
    }
  }

  @Override
  public String getBotUsername() {
    return botConfigComponent.getBotUsername();
  }

  @Override
  public String getBotToken() {
    return botConfigComponent.getBotToken();
  }
}
