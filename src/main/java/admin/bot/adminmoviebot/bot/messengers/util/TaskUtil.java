package admin.bot.adminmoviebot.bot.messengers.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class TaskUtil {

  //GET USER'S CHAT ID BASED ON MESSAGE TYPE
  public static Long getChatId(Update update) {
    long chatId = 0L;
    if (update.hasMessage()) {
      chatId = update.getMessage().getChatId();
    } else {
      chatId = update.getCallbackQuery().getMessage().getChatId();
    }
    return chatId;
  }
  public static String getChatIdStr(Update update) {
    Long chatId = 0L;
    if (update.hasMessage()) {
      chatId = update.getMessage().getChatId();
    } else {
      chatId = update.getCallbackQuery().getMessage().getChatId();
    }
    return String.valueOf(chatId);
  }

  // MAKE KeyboardButton AUTOMATICALLY
  public static ReplyKeyboardMarkup createTwoColumnKeyboard(String[] buttonNames) {
    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    replyKeyboardMarkup.setResizeKeyboard(true);
    replyKeyboardMarkup.setOneTimeKeyboard(false);
    replyKeyboardMarkup.setSelective(false);


    List<KeyboardRow> keyboardRows = new ArrayList<>();
    KeyboardRow row = new KeyboardRow();

    for (int i = 0; i < buttonNames.length; i++) {
      if (i % 2 == 0 && i != 0) {
        keyboardRows.add(row);
        row = new KeyboardRow();
      }
      row.add(new KeyboardButton(buttonNames[i]));
    }
    keyboardRows.add(row);

    replyKeyboardMarkup.setKeyboard(keyboardRows);

    return replyKeyboardMarkup;
  }


  // SIMPLE TEXT MESSAGE SENDER
  public static SendMessage messageSender(Update update, String message) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.enableHtml(true);
    sendMessage.setChatId(String.valueOf(getChatId(update)));
    sendMessage.setText(message);
    return sendMessage;
  }

  // MAKE InlineKeyboardButton AUTOMATICALLY
  public static InlineKeyboardMarkup createTwoColumnInlineKeyboard(String[] buttonNames, String[] callbackData) {
    if (buttonNames.length != callbackData.length) {
      throw new IllegalArgumentException("Button names and callback data arrays must have the same length");
    }

    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
    List<InlineKeyboardButton> row = new ArrayList<>();

    for (int i = 0; i < buttonNames.length; i++) {
      if (i % 2 == 0 && i != 0) {
        keyboardRows.add(row);
        row = new ArrayList<>();
      }
      InlineKeyboardButton button = new InlineKeyboardButton();
      button.setText(buttonNames[i]);
      button.setCallbackData(callbackData[i]);
      row.add(button);
    }
    keyboardRows.add(row);

    inlineKeyboardMarkup.setKeyboard(keyboardRows);
    return inlineKeyboardMarkup;
  }

  public static SendMessage removeKeyboardButtons(Update update){
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(String.valueOf(getChatId(update)));
    ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
    remove.setRemoveKeyboard(true);
    sendMessage.setReplyMarkup(remove);
    sendMessage.setText("   ");
    return sendMessage;
  }
}
