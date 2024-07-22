package admin.bot.adminmoviebot.bot.components;

import admin.bot.adminmoviebot.bot.MessageHandler;
import admin.bot.adminmoviebot.bot.config.BotConfigComponent;
import admin.bot.adminmoviebot.bot.messengers.channel.ChannelMessengers;
import admin.bot.adminmoviebot.bot.messengers.menu.MenuMessageSender;
import admin.bot.adminmoviebot.bot.messengers.newMovie.NewMovieMsgSender;
import admin.bot.adminmoviebot.dbConfig.service.movie.service.MovieService;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static admin.bot.adminmoviebot.bot.constants.BotState.ST_MENU;
import static admin.bot.adminmoviebot.bot.constants.Messages.MSG_START;

@Component
public class MainAdminComponent extends TelegramLongPollingBot {
  private final MovieService movieService;
  private final UserService userService;
  private final BotConfigComponent botConfigComponent;
  private final MenuMessageSender menuMessageSender;
  private final NewMovieMsgSender newMovieMsgSender;
  private final ChannelMessengers channelMessengers;
  private final MessageHandler messageHandler;
  private final CallbackHandler callbackHandler;
  private final ChannelMessageHandler channelMessageHandler;

  public MainAdminComponent(MovieService movieService, UserService userService, BotConfigComponent botConfigComponent,
                            MenuMessageSender menuMessageSender, NewMovieMsgSender newMovieMsgSender,
                            ChannelMessengers channelMessengers, MessageHandler messageHandler,
                            CallbackHandler callbackHandler, ChannelMessageHandler channelMessageHandler) {
    this.movieService = movieService;
    this.userService = userService;
    this.botConfigComponent = botConfigComponent;
    this.menuMessageSender = menuMessageSender;
    this.newMovieMsgSender = newMovieMsgSender;
    this.channelMessengers = channelMessengers;
    this.messageHandler = messageHandler;
    this.callbackHandler = callbackHandler;
    this.channelMessageHandler = channelMessageHandler;
  }

  @SneakyThrows
  @Override
  public void onUpdateReceived(Update update) {

    if (update.hasChannelPost()) {
      channelMessageHandler.handlerMessage(update, this);
    } else if (update.hasMessage()) {
      if (update.getMessage().hasText() && update.getMessage().getText().equals(MSG_START)) {
        execute(menuMessageSender.sendMenu(update));
        userService.changeUsersStateByUpdate(update, ST_MENU);
      } else {
        messageHandler.handleMessage(update, this);
      }
    } else if (update.hasCallbackQuery()) {
      callbackHandler.handleCallback(update, this);
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
