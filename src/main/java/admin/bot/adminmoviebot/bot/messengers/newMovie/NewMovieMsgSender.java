package admin.bot.adminmoviebot.bot.messengers.newMovie;

import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.service.MovieService;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

@Service
public final class NewMovieMsgSender implements NewMovieMsgSenderInt {

  private final MovieService movieService;

  public NewMovieMsgSender(MovieService movieService) {
    this.movieService = movieService;
  }


  //SEND GENERATED ID OF MOVIE
  @Override
  public SendMessage sendGeneratedMovieId(Update update) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    String code = movieService.generateUniqueCode();
    sendMessage.setText(STR."\{Messages.MSG_ID_FOR_NEW_MOVE}<b>\{code}</b>");
    sendMessage.enableHtml(true);
    String[] copyButton = {Buttons.BTN_COPY_CODE};
    String[] codeData = {code};
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(copyButton, codeData);
    sendMessage.setReplyMarkup(twoColumnInlineKeyboard);

    return sendMessage;
  }

  @Override
  public EditMessageText copyCodeSendName(Update update) {
    EditMessageText editMessageText = new EditMessageText();
    editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
    editMessageText.setChatId(TaskUtil.getChatIdStr(update));
    editMessageText.setText(Messages.MSG_MOVIE_NAME);

    //save code to clipboard
    String code = update.getCallbackQuery().getData();
    StringSelection stringSelection = new StringSelection(code);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection,null);


    return editMessageText;
  }
}
