package admin.bot.adminmoviebot.bot.messengers.newMovie;

import admin.bot.adminmoviebot.bot.config.BotConfigComponent;
import admin.bot.adminmoviebot.bot.constants.*;
import admin.bot.adminmoviebot.bot.messengers.category.CategoryMsgSender;
import admin.bot.adminmoviebot.bot.messengers.menu.MenuMessageSender;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.entity.Category;
import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import admin.bot.adminmoviebot.dbConfig.repository.CategoryRepository;
import admin.bot.adminmoviebot.dbConfig.service.category.service.CategoryService;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import admin.bot.adminmoviebot.dbConfig.service.movie.service.MovieService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;

import static admin.bot.adminmoviebot.bot.constants.BotState.*;

@Service
public final class NewMovieMsgSender implements NewMovieMsgSenderInt {

  private final MovieService movieService;
  private final CategoryRepository categoryRepository;
  private final BotConfigComponent botConfigComponent;
  private final UserService userService;
  private final MenuMessageSender menuMessageSender;
  private final CategoryService categoryService;

  private static String currentMovieCode = null;

  public NewMovieMsgSender(MovieService movieService, CategoryRepository categoryRepository, BotConfigComponent botConfigComponent, UserService userService, MenuMessageSender menuMessageSender, CategoryService categoryService) {
    this.movieService = movieService;
    this.categoryRepository = categoryRepository;
    this.botConfigComponent = botConfigComponent;
    this.userService = userService;
    this.menuMessageSender = menuMessageSender;
    this.categoryService = categoryService;
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


    userService.saveMovieCode(update, code);
    currentMovieCode = userService.getUsersMovieCode(update);
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(copyButton, codeData);
    sendMessage.setReplyMarkup(twoColumnInlineKeyboard);
    return sendMessage;
  }

  //SAVE MOVIE AND ADD MOVIE TO DATABASE
  @Override
  public SendMessage copyCodeSendName(Update update) {
    currentMovieCode = userService.getUsersMovieCode(update);
    SendMessage editMessageText = new SendMessage();
    editMessageText.setChatId(TaskUtil.getChatIdStr(update));
    editMessageText.setText(Messages.MSG_MOVIE_NAME);

    //save code to clipboard
    String code = update.getCallbackQuery().getData();
    StringSelection stringSelection = new StringSelection(code);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);

    ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
    replyKeyboardRemove.setRemoveKeyboard(true);
    replyKeyboardRemove.setSelective(false);
    editMessageText.setReplyMarkup(replyKeyboardRemove);

    //START TO SAVE MOVIE
    movieService.saveMovieCode(code);
    // SET USER'S PREVIOUS STATE
    userService.setUsersPreviousStateByChatId(update, ST_SAVE_NAME_FIRST);
    return editMessageText;
  }

  // SAVE NAME WHICH IS RECEIVED AND SEND CATEGORIES LIST
  @Override
  public SendMessage saveNameChooseCategory(Update update) {
    currentMovieCode = userService.getUsersMovieCode(update);
    SendMessage editMessageText = getCategoriesList(update);
    //SAVE MOVIE'S NAME
    if (userService.getUserPreviousState(TaskUtil.getChatId(update)).equals(ST_SAVE_NAME_FIRST)) {
      String name = update.getMessage().getText();
      movieService.saveMovieName(currentMovieCode, name);
    }

    userService.setUsersPreviousStateByChatId(update, ST_SAVE_CATEGORY_FIRST);

    return editMessageText;
  }

  private SendMessage getCategoriesList(Update update) {
    SendMessage editMessageText = new SendMessage();
    editMessageText.setChatId(TaskUtil.getChatIdStr(update));
    editMessageText.setText(Messages.MSG_CHOOSE_MOVIE_CATEGORY);

    // DRAW CATEGORIES INLINE BUTTONS
    String[] array = categoryService.getAllCategoriesName();
    String[] ids = categoryService.getAllCategoriesIds();

    InlineKeyboardMarkup categoriesButtons = TaskUtil.createTwoColumnInlineKeyboard(array, ids);
    editMessageText.setReplyMarkup(categoriesButtons);
    return editMessageText;
  }


  // SAVE CATEGORY AND SEND LANGUAGES LIST
  @Override
  public SendMessage saveCategoryChooseLang(Update update) {
    currentMovieCode = userService.getUsersMovieCode(update);
    SendMessage editMessageText = CategoryMsgSender.getLanguagesList(update, Messages.MSG_CHOOSE_LANG_MOVIE);
    //SAVE CATEGORY
    if (userService.getUserPreviousState(TaskUtil.getChatId(update)).equals(ST_SAVE_CATEGORY_FIRST)) {
      String data = update.getCallbackQuery().getData();
      movieService.saveMovieCategoryId(currentMovieCode, data);
    }
    // SET USER'S PREVIOUS STATE
    userService.setUsersPreviousStateByChatId(update, ST_SAVE_LANG_FIRST);

    return editMessageText;
  }


  // SAVE SELECTED LANG AND SEND QUALITIES LIST
  @Override
  public SendMessage saveLangChooseQuality(Update update) {
    currentMovieCode = userService.getUsersMovieCode(update);
    SendMessage editMessageText = new SendMessage();
    editMessageText.setChatId(TaskUtil.getChatIdStr(update));
    editMessageText.setText(Messages.MSG_CHOOSE_QUALITY);

    String[] btnQualities = Buttons.BTN_QUALITIES;
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(btnQualities, btnQualities);
    editMessageText.setReplyMarkup(twoColumnInlineKeyboard);


    //SAVE LANG OF MOVIE
    if (userService.getUserPreviousState(TaskUtil.getChatId(update)).equals(ST_SAVE_LANG_FIRST)) {
      movieService.saveMovieLanguage(currentMovieCode, update.getCallbackQuery().getData());
    }
    // SET USER'S PREVIOUS STATE
    userService.setUsersPreviousStateByChatId(update, ST_SAVE_QUALITY_FIRST);

    return editMessageText;
  }


  // SAVE QUALITY OF MOVIE AND ASK LENGTH OF MOVIE
  @Override
  public SendMessage saveQualitySendRunTime(Update update) {
    currentMovieCode = userService.getUsersMovieCode(update);
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(Messages.MSG_ENTER_LENGTH_MOVIE);
    if (userService.getUserPreviousState(TaskUtil.getChatId(update)).equals(ST_SAVE_QUALITY_FIRST)) {
      movieService.saveMovieQuality(currentMovieCode, update.getCallbackQuery().getData());
    }
    // SET USER'S PREVIOUS STATE
    userService.setUsersPreviousStateByChatId(update, ST_SAVE_RUNTIME_FIRST);
    return sendMessage;
  }


  // SAVE RUN TIME OF MOVIE AND ASK SIZE
  @Override
  public SendMessage saveMovieRunTimeSendSize(Update update) {
    currentMovieCode = userService.getUsersMovieCode(update);
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(Messages.MSG_ENTER_SIZE);

    //SAVE RUNTIME
    if (userService.getUserPreviousState(TaskUtil.getChatId(update)).equals(ST_SAVE_RUNTIME_FIRST)) {
      movieService.saveMovieRunTime(currentMovieCode, update.getMessage().getText());
    }

    // SET USER'S PREVIOUS STATE
    userService.setUsersPreviousStateByChatId(update, ST_SAVE_SIZE_FIRST);
    return sendMessage;
  }

  @Override
  public SendMessage saveSizeSendProductionYear(Update update) {
    currentMovieCode = userService.getUsersMovieCode(update);
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(Messages.MSG_ENTER_PRODUCTION_YEAR);
    sendMessage.enableHtml(true);
    // SAVE MOVIE'S SIZE
    if (userService.getUserPreviousState(TaskUtil.getChatId(update)).equals(ST_SAVE_SIZE_FIRST)) {
      movieService.saveMovieSize(currentMovieCode, update.getMessage().getText());
    }
    userService.setUsersPreviousStateByChatId(update, ST_SAVE_YEAR_FIRST);
    return sendMessage;
  }

  @Override
  public SendMessage saveMovieYearSendConfirmation(Update update) {
    currentMovieCode = userService.getUsersMovieCode(update);
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.enableHtml(true);
    if (ST_SAVE_YEAR_FIRST.equals(userService.getUserPreviousState(TaskUtil.getChatId(update)))) {
      movieService.saveMovieYear(currentMovieCode, update.getMessage().getText());
    }

    String message;
    if (update.hasCallbackQuery()) {
      message = update.getCallbackQuery().getData();
    } else {
      message = update.getMessage().getText();
    }
    BotState previousState = userService.getUserPreviousState(TaskUtil.getChatId(update));
    switch (previousState) {
      case ST_SAVE_NAME_EDIT -> {
        movieService.saveMovieName(currentMovieCode, message);
      }
      case ST_SAVE_CATEGORY_EDIT -> {
        movieService.saveMovieCategoryId(currentMovieCode, message);
      }
      case ST_SAVE_LANG_EDIT -> {
        movieService.saveMovieLanguage(currentMovieCode, message);
      }
      case ST_SAVE_QUALITY_EDIT -> {
        movieService.saveMovieQuality(currentMovieCode, message);
      }
      case ST_SAVE_YEAR_EDIT -> {
        movieService.saveMovieYear(currentMovieCode, message);
      }
      case ST_SAVE_SIZE_EDIT -> {
        movieService.saveMovieSize(currentMovieCode, message);
      }
      case ST_SAVE_DURATION_EDIT -> {
        movieService.saveMovieRunTime(currentMovieCode, message);
      }
    }


    Movie movie = movieService.getMovie(currentMovieCode);
    StringBuilder movieDetails = getMoviesDetails(movie);
    sendMessage.setText(String.valueOf(movieDetails));
    String[] confirmButtons = {Buttons.BTN_YES, Buttons.BTN_NO, Buttons.BTN_EDIT};
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(confirmButtons, confirmButtons);
    sendMessage.setReplyMarkup(twoColumnInlineKeyboard);
    return sendMessage;
  }

  private StringBuilder getMoviesDetails(Movie movie) {
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
    String[] parameters = {Buttons.BTN_MOVIE_NAME, Buttons.BTN_MOVIE_GENRE, Buttons.BTN_MOVIE_LANG, Buttons.BTN_MOVIE_QUALITY, Buttons.BTN_MOVIE_YEAR, Buttons.BTN_MOVIE_SIZE, Buttons.BTN_MOVIE_DURATION};
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(parameters, parameters);
    sendMessage.setReplyMarkup(twoColumnInlineKeyboard);
    return sendMessage;
  }

  // RECEIVE EXACT PARAMETER TO EDIT AND CHANGE STATE
  @Override
  public SendMessage editExactParameter(Update update) {
    SendMessage sendMessage = new SendMessage();
    String data = update.getCallbackQuery().getData();
    userService.setUsersStateByUpdate(update, ST_MOVIE_CONFIRM);

    switch (data) {
      case Buttons.BTN_MOVIE_NAME -> {
        sendMessage = copyCodeSendName(update);
        userService.setUsersPreviousStateByChatId(update, ST_SAVE_NAME_EDIT);
      }
      case Buttons.BTN_MOVIE_GENRE -> {
        sendMessage = saveNameChooseCategory(update);
        userService.setUsersPreviousStateByChatId(update, ST_SAVE_CATEGORY_EDIT);
      }
      case Buttons.BTN_MOVIE_LANG -> {
        sendMessage = saveCategoryChooseLang(update);
        userService.setUsersPreviousStateByChatId(update, ST_SAVE_LANG_EDIT);
      }
      case Buttons.BTN_MOVIE_QUALITY -> {
        sendMessage = saveLangChooseQuality(update);
        userService.setUsersPreviousStateByChatId(update, ST_SAVE_QUALITY_EDIT);
      }
      case Buttons.BTN_MOVIE_YEAR -> {
        sendMessage = saveSizeSendProductionYear(update);
        userService.setUsersPreviousStateByChatId(update, ST_SAVE_YEAR_EDIT);
      }
      case Buttons.BTN_MOVIE_SIZE -> {
        sendMessage = saveMovieRunTimeSendSize(update);
        userService.setUsersPreviousStateByChatId(update, ST_SAVE_SIZE_EDIT);
      }
      case Buttons.BTN_MOVIE_DURATION -> {
        sendMessage = saveQualitySendRunTime(update);
        userService.setUsersPreviousStateByChatId(update, ST_SAVE_DURATION_EDIT);
      }
    }
    return sendMessage;
  }

  @Override
  public SendMessage sendMovieDetails(Update update, String message) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(Objects.requireNonNullElse(message, Messages.MSG_CHOOSE_DOWN));
    String[] parameters = {Buttons.BTN_ADD_NEW_MOVIE, Buttons.BTN_MOVIE_LIST, Buttons.BTN_BACK};
    ReplyKeyboardMarkup twoColumnKeyboard = TaskUtil.createTwoColumnKeyboard(parameters);
    sendMessage.setReplyMarkup(twoColumnKeyboard);
    userService.setUsersStateByUpdate(update, ST_MOVIE_OPTIONS);
    return sendMessage;
  }

  @Override
  public SendMessage movieFunctions(Update update) {
    String message = update.getMessage().getText();
    SendMessage sendMessage = new SendMessage();
    switch (message) {
      case Buttons.BTN_ADD_NEW_MOVIE -> {
        if (movieService.getMoviePostIsNull() != null) {
          Movie moviePostIsNull = movieService.getMoviePostIsNull();
          userService.setUsersStateByUpdate(update, ST_MOVIE_OPTIONS);
          sendMessage = sendMovieDetails(update, moviePostIsNull.getMovieCode() + Messages.MSG_RESTRICT_ADD_MOVIE);
        } else {
          sendMessage = sendGeneratedMovieId(update);
          userService.setUsersStateByUpdate(update, ST_MOVIE_CODE);
        }
      }
      case Buttons.BTN_MOVIE_LIST -> {
        sendMessage = drawAllMovieCategories(update);
      }
      case Buttons.BTN_BACK -> {
        sendMessage = menuMessageSender.sendMenu(update);
        userService.setUsersStateByUpdate(update, ST_MENU);
      }
    }
    return sendMessage;
  }

  @Override
  public SendMessage drawAllMovieCategories(Update update) {
    SendMessage sendMessage = getCategoriesList(update);
    InlineKeyboardMarkup inlineKeyboardMarkup = (InlineKeyboardMarkup) sendMessage.getReplyMarkup();
    List<List<InlineKeyboardButton>> keyboard = inlineKeyboardMarkup.getKeyboard();
    List<InlineKeyboardButton> row = new ArrayList<>();
    InlineKeyboardButton button = new InlineKeyboardButton();
    button.setText(Buttons.BTN_BACK);
    button.setCallbackData(Buttons.BTN_BACK);
    row.add(button);
    keyboard.add(row);
    sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    userService.setUsersStateByUpdate(update, ST_MOVIE_CATEGORY_TO_GET_LIST);
    return sendMessage;
  }

  // DRAW MOVIES LIST
  @Override
  public List<SendMessage> drawMovieByCategory(Update update) {
    List<SendMessage> sendMessages = new ArrayList<>();
    SendMessage sendMessage;
    List<Movie> movieList = null;
    String data = update.getCallbackQuery().getData();
    if (data.equals(Buttons.BTN_BACK)) {
      sendMessage = sendMovieDetails(update, null);
      sendMessages.add(sendMessage);
    } else {
      movieList = movieService.getMoviesByCategoryId(Long.valueOf(data));
      if ((!movieList.isEmpty())) {
        movieList.forEach(movie -> {

          StringBuilder moviesDetails = getMoviesDetails(movie);
          SendMessage moviesSendMessage = new SendMessage();
          moviesSendMessage.setChatId(TaskUtil.getChatIdStr(update));
          moviesSendMessage.setText(moviesDetails.toString());
          moviesSendMessage.enableHtml(true);

          String trailerUrl = movieService.getTrailersUrl(movie);
          String[] buttons;
          String[] buttonData;
          buttons = new String[]{Buttons.BTN_DELETE};
          buttonData = new String[]{String.valueOf(movie.getId())};

          InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(buttons, buttonData);
          if (trailerUrl != null) {
            List<List<InlineKeyboardButton>> keyboard = twoColumnInlineKeyboard.getKeyboard();
            List<InlineKeyboardButton> inlineKeyboardButtons = keyboard.get(0);

            // GET INLINE BUTTONS AND SET URL AND DELETE BUTTONS
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(Buttons.BTN_ROUTE_TRAILER);
            inlineKeyboardButton.setUrl(trailerUrl);
            inlineKeyboardButtons.add(inlineKeyboardButton);
            keyboard.set(0,inlineKeyboardButtons);
          }

          moviesSendMessage.setReplyMarkup(twoColumnInlineKeyboard);

          sendMessages.add(moviesSendMessage);
          userService.setUsersStateByUpdate(update, ST_MOVIE_CATEGORY_TO_GET_LIST);
          userService.setUsersPreviousStateByChatId(update,ST_DELETE_OR_ROUTE);
        });
        sendMessage = drawAllMovieCategories(update);
        sendMessages.add(sendMessage);
      } else {
        SendMessage message = TaskUtil.messageSender(update, Messages.MSG_MOVIE_NOT_FOUND);
        sendMessages.add(message);
        sendMessage = drawAllMovieCategories(update);
        sendMessages.add(sendMessage);
        userService.setUsersStateByUpdate(update, ST_MOVIE_CATEGORY_TO_GET_LIST);
      }
    }
    return sendMessages;
  }

  // DELETE SELECTED MOVIE OR ROUTE USER
  @Override
  public DeleteMessage deleteOrRouteTrailer(Update update) {
    DeleteMessage deleteMessage = new DeleteMessage();
    String data = update.getCallbackQuery().getData();
    boolean convertibleToLong = CategoryMsgSender.isConvertibleToLong(data);
    if (convertibleToLong) {
      movieService.deleteMovie(data);
      deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
      deleteMessage.setChatId(TaskUtil.getChatIdStr(update));
    }
    return deleteMessage;
  }
}
