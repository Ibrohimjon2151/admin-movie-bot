package admin.bot.adminmoviebot.bot;

import admin.bot.adminmoviebot.bot.component.BotConfigComponent;
import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.menu.MenuMessageSender;
import admin.bot.adminmoviebot.bot.messengers.newMovie.NewMovieMsgSender;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.service.UserService;
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
        userService.changeUsersStateByChatId(update,ST_MENU);
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
            userService.changeUsersStateByChatId(update,ST_CHOOSE_LANGUAGE);
          }
        }
      }
    } else if (update.hasCallbackQuery()) {
      String data = update.getCallbackQuery().getData();
      switch (userState) {
        case ST_MOVIE_CODE -> {
          userService.changeUsersStateByChatId(update,ST_ENTER_MOVIE_NAME);
          execute(newMovieMsgSender.copyCodeSendName(update));
        }
        case ST_CHOOSE_LANGUAGE -> {
          userService.changeUsersStateByChatId(update,ST_MOVIE_QUALITY);
          execute(newMovieMsgSender.saveCategoryChooseLang(update));
        }
        case ST_MOVIE_QUALITY -> {
          userService.changeUsersStateByChatId(update,ST_MOVIE_SIZE);
          execute(newMovieMsgSender.saveLangChooseQuality(update));
        }
        case ST_MOVIE_SIZE->{
          userService.changeUsersStateByChatId(update,ST_MOVIE_LENGTH);
          execute(menuMessageSender.deleteMessage(update));
          execute(newMovieMsgSender.saveQualitySendRunTime(update));
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
