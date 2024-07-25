package admin.bot.adminmoviebot.bot.components;

import admin.bot.adminmoviebot.bot.config.UsersBotConfig;
import admin.bot.adminmoviebot.dbConfig.entity.user.bot.User;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@Qualifier("usersBotComponent")
public class UsersBotComponent extends TelegramLongPollingBot {

  private final UsersBotConfig usersBotConfig;
  private final UserService userService;

  public UsersBotComponent(UsersBotConfig usersBotConfig, UserService userService) {
    this.usersBotConfig = usersBotConfig;
    this.userService = userService;
  }

  @SneakyThrows
  @Override
  public void onUpdateReceived(Update update) {

  }

  public void sendTextFormatMessage(String message, String onlyUser,Integer messageId) {
    try {
      if (onlyUser != null) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setReplyToMessageId(messageId);
        sendMessage.setChatId(onlyUser);
        execute(sendMessage); // Sending the message
      } else {
        List<User> allUser = userService.getAll();
        for (User user : allUser) {
          SendMessage sendMessage = new SendMessage();
          sendMessage.setText(message);
          sendMessage.setChatId(String.valueOf(user.getChatId()));
          execute(sendMessage); // Sending the message
        }
      }
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }

  public void sendPhotoFormatMessage(Update update, InputFile inputFile, String onlyUser) throws TelegramApiException {
    if (onlyUser != null) {
      SendPhoto sendPhoto = new SendPhoto();
      sendPhoto.setChatId(onlyUser);
      sendPhoto.setCaption(update.getMessage().getCaption());
      sendPhoto.setPhoto(inputFile);
      execute(sendPhoto);
    } else {
      List<User> allUser = userService.getAll();
      for (User user : allUser) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(user.getChatId()));
        sendPhoto.setCaption(update.getMessage().getCaption());
        sendPhoto.setPhoto(inputFile);
        execute(sendPhoto);
      }
    }
  }

  public void sendVideoFormatMessage(Update update, InputFile inputFile, String onlyUser) throws TelegramApiException {
    if (onlyUser != null) {
      SendVideo sendVideo = new SendVideo();
      sendVideo.setCaption(update.getMessage().getCaption());
      sendVideo.setVideo(inputFile);
      sendVideo.setChatId(onlyUser);
      execute(sendVideo);
    } else {
      List<User> users = userService.getAll();
      for (User user : users) {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setCaption(update.getMessage().getCaption());
        sendVideo.setVideo(inputFile);
        sendVideo.setChatId(String.valueOf(user.getChatId()));
        execute(sendVideo);
      }
    }
  }

  public void forwardMessageToUsers(List<ForwardMessage> forwardMessages) throws TelegramApiException {
    for (ForwardMessage forwardMessage : forwardMessages) {
      execute(forwardMessage);
    }
  }

  @Override
  public String getBotUsername() {
    return usersBotConfig.getBotUsername();
  }

  @Override
  public String getBotToken() {
    return usersBotConfig.getBotToken();
  }

}
