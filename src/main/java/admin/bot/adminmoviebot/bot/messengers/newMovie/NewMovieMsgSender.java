package admin.bot.adminmoviebot.bot.messengers.newMovie;

import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.Language;
import admin.bot.adminmoviebot.bot.constants.LanguageCode;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.entity.Category;
import admin.bot.adminmoviebot.dbConfig.repository.CategoryRepository;
import admin.bot.adminmoviebot.dbConfig.service.MovieService;
import org.hibernate.Session;
import org.hibernate.UnknownProfileException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;

@Service
public final class NewMovieMsgSender implements NewMovieMsgSenderInt {

  private final MovieService movieService;
  private final CategoryRepository categoryRepository;
  private static String currentMovieCode = null;

  public NewMovieMsgSender(MovieService movieService, CategoryRepository categoryRepository) {
    this.movieService = movieService;
    this.categoryRepository = categoryRepository;
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

  //SAVE MOVIE AND ADD MOVIE TO DATABASE
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
    clipboard.setContents(stringSelection, null);

    //START TO SAVE MOVIE
    movieService.saveMovieCode(code);
    currentMovieCode = code;

    return editMessageText;
  }

  // SAVE NAME WHICH IS RECEIVED AND SEND CATEGORIES LIST
  @Override
  public SendMessage saveNameChooseCategory(Update update) {
    SendMessage editMessageText = new SendMessage();
    editMessageText.setChatId(TaskUtil.getChatIdStr(update));
    editMessageText.setText(Messages.MSG_CHOOSE_MOVIE_CATEGORY);

    // DRAW CATEGORIES INLINE BUTTONS
    List<Category> allCategories = categoryRepository.findAll();
    String[] array = allCategories.stream().map(Category::getName)
      .toArray(String[]::new);
    String[] ids = allCategories.stream()
      .map(category -> String.valueOf(category.getId())) // Convert id to string
      .toArray(String[]::new);
    InlineKeyboardMarkup categoriesButtons = TaskUtil.createTwoColumnInlineKeyboard(array, ids);
    editMessageText.setReplyMarkup(categoriesButtons);

    //SAVE MOVIE'S NAME
    String name = update.getMessage().getText();
    movieService.saveMovieName(currentMovieCode, name);

    return editMessageText;
  }


  // SAVE CATEGORY AND SEND LANGUAGES LIST
  @Override
  public EditMessageText saveCategoryChooseLang(Update update) {
    EditMessageText editMessageText = new EditMessageText();
    editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
    editMessageText.setChatId(TaskUtil.getChatIdStr(update));
    editMessageText.setText(Messages.MSG_CHOOSE_LANG);

    Language[] values = Language.values();
    String[] languages = Arrays.stream(values)
      .map(Enum::name)
      .toArray(String[]::new);
    String[] langButtons = {Buttons.BTN_UZB_FLAG,Buttons.BTN_ENG_FLAG,Buttons.BTN_RU_FLAG};
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(langButtons, languages);
    editMessageText.setReplyMarkup(twoColumnInlineKeyboard);

    //SAVE CATEGORY
    String data = update.getCallbackQuery().getData();
    movieService.saveMovieCategoryId(currentMovieCode,data);

    return editMessageText;
  }


  // SAVE SELECTED LANG AND SEND QUALITIES LIST
  @Override
  public EditMessageText saveLangChooseQuality(Update update) {
    EditMessageText editMessageText = new EditMessageText();
    editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
    editMessageText.setChatId(TaskUtil.getChatIdStr(update));
    editMessageText.setText(Messages.MSG_CHOOSE_QUALITY);

    String[] btnQualities = Buttons.BTN_QUALITIES;
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(btnQualities, btnQualities);
    editMessageText.setReplyMarkup(twoColumnInlineKeyboard);

    //SAVE LANG OF MOVIE
    movieService.saveMovieLanguage(currentMovieCode,update.getCallbackQuery().getData());
    return editMessageText;
  }

  // SAVE QUALITY OF MOVIE AND ASK LENGTH OF MOVIE


  @Override
  public SendMessage saveQualitySendRunTime(Update update) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(Messages.MSG_ASK_ENTER_LENGTH_MOVIE);
    movieService.saveMovieQuality(currentMovieCode,update.getCallbackQuery().getData());
    return sendMessage;
  }
}
