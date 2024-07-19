package admin.bot.adminmoviebot.bot.messengers.newMovie;

import admin.bot.adminmoviebot.bot.component.BotConfigComponent;
import admin.bot.adminmoviebot.bot.constants.*;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.entity.Category;
import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import admin.bot.adminmoviebot.dbConfig.repository.CategoryRepository;
import admin.bot.adminmoviebot.dbConfig.service.UserService;
import admin.bot.adminmoviebot.dbConfig.service.movie.service.MovieService;
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

import static admin.bot.adminmoviebot.bot.constants.BotState.*;

@Service
public final class NewMovieMsgSender implements NewMovieMsgSenderInt {

  private final MovieService movieService;
  private final CategoryRepository categoryRepository;
  private static String currentMovieCode = null;
  private final BotConfigComponent botConfigComponent;
  private final UserService userService;

  public NewMovieMsgSender(MovieService movieService, CategoryRepository categoryRepository, BotConfigComponent botConfigComponent, UserService userService) {
    this.movieService = movieService;
    this.categoryRepository = categoryRepository;
    this.botConfigComponent = botConfigComponent;
    this.userService = userService;
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
    //START TO SAVE MOVIE
    movieService.saveMovieCode(code);
    currentMovieCode = code;
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(copyButton, codeData);
    sendMessage.setReplyMarkup(twoColumnInlineKeyboard);
    return sendMessage;
  }

  //SAVE MOVIE AND ADD MOVIE TO DATABASE
  @Override
  public SendMessage copyCodeSendName(Update update) {
    SendMessage editMessageText = new SendMessage();
    editMessageText.setChatId(TaskUtil.getChatIdStr(update));
    editMessageText.setText(Messages.MSG_MOVIE_NAME);

    //save code to clipboard
    String code = update.getCallbackQuery().getData();
    StringSelection stringSelection = new StringSelection(code);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);

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
    String[] languages = {LanguageCode.LANG_UZB, LanguageCode.LANG_ENG, LanguageCode.LANG_RU};
    String[] langButtons = {Buttons.BTN_UZB_FLAG, Buttons.BTN_ENG_FLAG, Buttons.BTN_RU_FLAG};
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(langButtons, languages);
    editMessageText.setReplyMarkup(twoColumnInlineKeyboard);

    //SAVE CATEGORY
    String data = update.getCallbackQuery().getData();
    movieService.saveMovieCategoryId(currentMovieCode, data);

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
    movieService.saveMovieLanguage(currentMovieCode, update.getCallbackQuery().getData());
    return editMessageText;
  }


  // SAVE QUALITY OF MOVIE AND ASK LENGTH OF MOVIE
  @Override
  public SendMessage saveQualitySendRunTime(Update update) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(Messages.MSG_ENTER_LENGTH_MOVIE);
    movieService.saveMovieQuality(currentMovieCode, update.getCallbackQuery().getData());
    return sendMessage;
  }


  // SAVE RUN TIME OF MOVIE AND ASK SIZE
  @Override
  public SendMessage saveMovieRunTimeSendSize(Update update) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(Messages.MSG_ENTER_SIZE);

    //SAVE RUNTIME
    movieService.saveMovieRunTime(currentMovieCode, update.getMessage().getText());
    return sendMessage;
  }

  @Override
  public SendMessage saveSizeSendProductionYear(Update update) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(Messages.MSG_ENTER_PRODUCTION_YEAR);
    sendMessage.enableHtml(true);

    movieService.saveMovieSize(currentMovieCode, update.getMessage().getText());
    return sendMessage;
  }

  @Override
  public SendMessage saveMovieYearSendConfirmation(Update update) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.enableHtml(true);
    movieService.saveMovieYear(currentMovieCode, update.getMessage().getText());
    Movie movie = movieService.getMovie(currentMovieCode);
    StringBuilder movieDetails = getStringBuilder(movie);
    sendMessage.setText(String.valueOf(movieDetails));
    String[] confirmButtons = {Buttons.BTN_YES, Buttons.BTN_NO, Buttons.BTN_EDIT};
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(confirmButtons, confirmButtons);
    sendMessage.setReplyMarkup(twoColumnInlineKeyboard);
    return sendMessage;
  }

  private StringBuilder getStringBuilder(Movie movie) {
    int lang = movie.getLanguage();

    StringBuilder movieDetails = new StringBuilder();

    movieDetails.append(STR."<b>\{movie.getName()}</b> \n\n");

    movieDetails.append(STR."\{MovieDetailsMsg.DTS_MOVIE_CODE[lang]} - ");
    movieDetails.append(STR."<b>\{movie.getMovieCode()}</b> \n\n");

    movieDetails.append(STR."\{MovieDetailsMsg.DTS_MOVIE_GENRE[lang]} - ");
    movieDetails.append(STR."<b>\{movie.getCategory().getName()}</b> \n");

    movieDetails.append(STR."\{MovieDetailsMsg.DTS_MOVIE_LANG[lang]} - ");
    movieDetails.append(STR."<b>\{MovieDetailsMsg.DTS_LANGUAGES[lang]}</b>\n");

    movieDetails.append(STR."\{MovieDetailsMsg.DTS_MOVIE_QUALITY[lang]} - ");
    movieDetails.append(STR."<b>\{movie.getQuality()}p</b>\n");

    movieDetails.append(STR."\{MovieDetailsMsg.DTS_MOVIE_YEAR[lang]} - ");
    movieDetails.append(STR."<b>\{movie.getProductionYear()}</b>\n");

    movieDetails.append(STR."\{MovieDetailsMsg.DTS_MOVIE_SIZE[lang]} - ");
    movieDetails.append(STR."<b>\{movie.getSize()}</b>\n");

    movieDetails.append(STR."\{MovieDetailsMsg.DTS_MOVIE_RUNTIME[lang]} - ");
    movieDetails.append(STR."<b>\{movie.getRunTime()}</b>\n\n");

    movieDetails.append(STR."\{MovieDetailsMsg.DTS_OFFICIAL_URL[lang]}\n");
    movieDetails.append(STR."@\{botConfigComponent.getBotUsername()}");
    return movieDetails;
  }


  // SEND PARAMETERS LIST TO EDIT
  @Override
  public EditMessageText sendEditParameters(Update update) {



    EditMessageText sendMessage = new EditMessageText();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
    sendMessage.setText(Messages.MSG_SELECT_PARAMETER);
    String[] parameters = {Buttons.BTN_MOVIE_NAME,Buttons.BTN_MOVIE_GENRE,Buttons.BTN_MOVIE_LANG,Buttons.BTN_MOVIE_QUALITY,Buttons.BTN_MOVIE_YEAR,Buttons.BTN_MOVIE_SIZE,Buttons.BTN_MOVIE_DURATION};
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(parameters, parameters);
    sendMessage.setReplyMarkup(twoColumnInlineKeyboard);
    return sendMessage;
  }

  // RECEIVE EXACT PARAMETER TO EDIT AND CHANGE STATE
  @Override
  public SendMessage editExactParameter(Update update) {
    SendMessage sendMessage = new SendMessage();
    String data = update.getCallbackQuery().getData();
    switch (data){
      case Buttons.BTN_MOVIE_NAME -> {
        userService.changeUsersStateByChatId(update,ST_MOVIE_CONFIRM);
        sendMessage = copyCodeSendName(update);
      }
    }
    return sendMessage;
  }
}
