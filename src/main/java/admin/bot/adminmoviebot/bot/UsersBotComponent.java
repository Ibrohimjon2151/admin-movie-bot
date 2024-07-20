package admin.bot.adminmoviebot.bot;

import admin.bot.adminmoviebot.bot.component.UsersBotConfig;
import admin.bot.adminmoviebot.dbConfig.entity.userBot.User;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class UsersBotComponent extends TelegramLongPollingBot {

  private final UsersBotConfig usersBotConfig;
  private final UserService userService;

  public UsersBotComponent(UsersBotConfig usersBotConfig, UserService userService) {
    this.usersBotConfig = usersBotConfig;
    this.userService = userService;
  }

  @Override
  public void onUpdateReceived(Update update) {
  }

  public void sendTextFormatMessage(String message) {
    try {
      List<User> allUser = userService.getAll();
      for (User user : allUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        execute(sendMessage); // Sending the message
      }
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }

  public void sendPhotoFormatMessage(Update update) throws TelegramApiException {
    List<User> allUser = userService.getAll();
    for (User user : allUser) {
      SendPhoto sendPhoto = new SendPhoto();
      List<PhotoSize> photos = update.getMessage().getPhoto();
      sendPhoto.setChatId(String.valueOf(user.getChatId()));

      PhotoSize largestPhoto = photos.get(photos.size() - 1);
      String fileId = largestPhoto.getFileId();

      sendPhoto.setCaption(update.getMessage().getCaption());

      sendPhoto.setPhoto(new InputFile(fileId));
      execute(sendPhoto);
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
