package admin.bot.adminmoviebot.bot;

import admin.bot.adminmoviebot.bot.component.BotConfigComponent;
import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.category.CategoryMsgSender;
import admin.bot.adminmoviebot.bot.messengers.channel.ChannelMessengers;
import admin.bot.adminmoviebot.bot.messengers.menu.MenuMessageSender;
import admin.bot.adminmoviebot.bot.messengers.newMovie.NewMovieMsgSender;
import admin.bot.adminmoviebot.bot.messengers.send.message.users.SendMessageUsers;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import lombok.SneakyThrows;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static admin.bot.adminmoviebot.bot.constants.BotState.*;
import static admin.bot.adminmoviebot.bot.constants.Messages.MSG_ENTER_WANTED_MESSAGE;
import static admin.bot.adminmoviebot.bot.constants.Messages.MSG_START;

@Component
public class MainAdminComponent extends TelegramLongPollingBot {

 private final UsersBotComponent usersBotComponent;
 private final BotConfigComponent botConfigComponent;
 private final MenuMessageSender menuMessageSender;
 private final UserService userService;
 private final NewMovieMsgSender newMovieMsgSender;
 private final CategoryMsgSender categoryMsgSender;
 private final ChannelMessengers channelMessengers;

 public MainAdminComponent(UsersBotComponent usersBotComponent, BotConfigComponent botConfigComponent, MenuMessageSender menuMessageSender, UserService userService, NewMovieMsgSender newMovieMsgSender, CategoryMsgSender categoryMsgSender, ChannelMessengers channelMessengers) {
  this.usersBotComponent = usersBotComponent;
  this.botConfigComponent = botConfigComponent;
  this.menuMessageSender = menuMessageSender;
  this.userService = userService;
  this.newMovieMsgSender = newMovieMsgSender;
  this.categoryMsgSender = categoryMsgSender;
  this.channelMessengers = channelMessengers;
 }


 String message = "";
 BotState userState;

 @SneakyThrows
 @Override
 public void onUpdateReceived(Update update) {
  if (update.hasChannelPost()) {
   for (ForwardMessage forwardMessage : channelMessengers.forwardMessageToAdmins(update)) {
    execute(forwardMessage);
   }
   for (SendMessage sendMessage : channelMessengers.handlePostFromChannel(update)) {
    execute(sendMessage);
   }
  } else if (update.hasMessage()) {
   userState = userService.getUsersStateByChatId(TaskUtil.getChatId(update));
   if (update.getMessage().hasText()) {
    message = update.getMessage().getText();
   }
   if (message.equals(MSG_START)) {
    execute(menuMessageSender.sendMenu(update));
    userService.changeUsersStateByUpdate(update, ST_MENU);
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
        userService.changeUsersStateByUpdate(update, ST_MOVIE_CODE);
       }
       case Buttons.BTN_SEND_MSG_USERS -> {
        userService.changeUsersStateByUpdate(update, ST_SEND_MESSAGE_USERS);
        execute(TaskUtil.messageSender(update, MSG_ENTER_WANTED_MESSAGE));
       }
       case Buttons.BTN_ADD_NEW_CATEGORY -> {
        userService.changeUsersStateByUpdate(update, ST_NEW_CATEGORY);
        execute(categoryMsgSender.sendAllCategoriesList(update));
       }
      }
     }
     case ST_ENTER_MOVIE_NAME -> {
      execute(menuMessageSender.deleteMessage(update));
      execute(newMovieMsgSender.saveNameChooseCategory(update));
      userService.changeUsersStateByUpdate(update, ST_CHOOSE_LANGUAGE);
     }
     case ST_MOVIE_SIZE -> {
      userService.changeUsersStateByUpdate(update, ST_MOVIE_YEAR);
      execute(newMovieMsgSender.saveMovieRunTimeSendSize(update));
     }
     case ST_MOVIE_YEAR -> {
      userService.changeUsersStateByUpdate(update, ST_MOVIE_CONFIRM);
      execute(newMovieMsgSender.saveSizeSendProductionYear(update));
     }
     case ST_MOVIE_CONFIRM -> {
      userService.changeUsersStateByUpdate(update, ST_REVIEW_ADDED_MOVIE);
      execute(TaskUtil.messageSender(update, Messages.MSG_CONFIRM_DETAILS_MOVIE));
      execute(newMovieMsgSender.saveMovieYearSendConfirmation(update));
     }
     case ST_ENTER_NEW_CATEGORY_NAME -> {
      userService.changeUsersStateByUpdate(update, ST_NEW_CATEGORY);
      categoryMsgSender.saveCategoryName(update);
      execute(TaskUtil.messageSender(update, Messages.MSG_SAVED_MOVIE));
      execute(categoryMsgSender.sendAllCategoriesList(update));
     }
     case ST_SEND_MESSAGE_USERS -> {
      if (update.getMessage().hasPhoto()) {
       InputFile inputFile = downloadPhoto(update);
       usersBotComponent.sendPhotoFormatMessage(update, inputFile);
      } else if (update.getMessage().hasVideo()) {
       InputFile inputFile = downloadPhoto(update);
       usersBotComponent.sendVideoFormatMessage(update, inputFile);
      } else {
       usersBotComponent.sendTextFormatMessage(update.getMessage().getText());
      }
      execute(TaskUtil.messageSender(update, Messages.MSG_MESSAGE_SENT));
      userService.changeUsersStateByUpdate(update, ST_MENU);
      execute(menuMessageSender.sendMenu(update));
     }
    }
   }
  } else if (update.hasCallbackQuery()) {
   userState = userService.getUsersStateByChatId(TaskUtil.getChatId(update));
   String data = update.getCallbackQuery().getData();
   switch (userState) {
    case ST_MOVIE_CODE -> {
     userService.changeUsersStateByUpdate(update, ST_ENTER_MOVIE_NAME);
     execute(menuMessageSender.deleteMessage(update));
     execute(newMovieMsgSender.copyCodeSendName(update));
    }
    case ST_CHOOSE_LANGUAGE -> {
     userService.changeUsersStateByUpdate(update, ST_MOVIE_QUALITY);
     execute(menuMessageSender.deleteMessage(update));
     execute(newMovieMsgSender.saveCategoryChooseLang(update));
    }
    case ST_MOVIE_QUALITY -> {
     userService.changeUsersStateByUpdate(update, ST_MOVIE_RUNTIME);
     execute(menuMessageSender.deleteMessage(update));
     execute(newMovieMsgSender.saveLangChooseQuality(update));
    }
    case ST_MOVIE_RUNTIME -> {
     userService.changeUsersStateByUpdate(update, ST_MOVIE_SIZE);
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
       userService.changeUsersStateByUpdate(update, ST_MENU);
      }
      case Buttons.BTN_NO -> {
       execute(menuMessageSender.deleteMessage(update));
       execute(TaskUtil.messageSender(update, Messages.MSG_MOVIE_DETAILS_DELETED));
       execute(menuMessageSender.sendMenu(update));
       userService.changeUsersStateByUpdate(update, ST_MENU);
      }
      case Buttons.BTN_EDIT -> {
       userService.changeUsersStateByUpdate(update, ST_EDIT_MOVIE);
       execute(newMovieMsgSender.sendEditParameters(update));
      }
     }
    }
    case ST_MOVIE_CONFIRM -> {
     userService.changeUsersStateByUpdate(update, ST_REVIEW_ADDED_MOVIE);
     execute(TaskUtil.messageSender(update, Messages.MSG_CONFIRM_DETAILS_MOVIE));
     execute(newMovieMsgSender.saveMovieYearSendConfirmation(update));
    }
    case ST_EDIT_MOVIE -> {
     execute(menuMessageSender.deleteMessage(update));
     execute(newMovieMsgSender.editExactParameter(update));
    }
    case ST_NEW_CATEGORY -> {
     execute(menuMessageSender.deleteMessage(update));
     execute(categoryMsgSender.deleteOrAddCategory(update));
    }
    case ST_DO_FORWARD -> {
     switch (data) {
      case Buttons.DATA_PERMIT_FORWARD -> {
       execute(menuMessageSender.deleteMessage(update));
       usersBotComponent.forwardMessageToUsers(channelMessengers.forwardSelectedMessageToUsers());
      }
      case Buttons.DATA_BANNED_FORWARD -> {
       for (DeleteMessage deleteMessagesAllAdmin : menuMessageSender.deleteMessagesAllAdmins(update)) {
        execute(deleteMessagesAllAdmin);
       }
      }
     }
     for (Long l : userService.getAdminsUserId()) {
      userService.changeUsersStateByChatId(l, ST_MENU);
     }
    }
   }
  }
 }

 public InputFile downloadPhoto(Update update) throws TelegramApiException, IOException {
  String fileId = "";
  if (update.getMessage().hasPhoto()) {
   List<PhotoSize> photos = update.getMessage().getPhoto();
   PhotoSize largestPhoto = photos.get(photos.size() - 1);
   fileId = largestPhoto.getFileId();
  }
  if (update.getMessage().hasVideo()) {
   fileId = update.getMessage().getVideo().getFileId();
  }
  GetFile getFile = new GetFile();
  getFile.setFileId(fileId);
  File file = execute(getFile);
  String filePath = file.getFilePath();

  URL url = new URL(STR."https://api.telegram.org/file/bot\{botConfigComponent.getBotToken()}/\{filePath}");
  InputStream in = url.openStream();
  java.io.File tempFile = java.io.File.createTempFile("photo", filePath.substring(filePath.lastIndexOf('.')));

  Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
  in.close();

  return new InputFile(tempFile);
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
