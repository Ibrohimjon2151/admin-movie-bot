package admin.bot.adminmoviebot.bot.messengers.analyse;

import admin.bot.adminmoviebot.bot.components.UsersBotComponent;
import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.admin.AdminSendMessenger;
import admin.bot.adminmoviebot.bot.messengers.menu.MenuMessageSender;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.entity.Comment;
import admin.bot.adminmoviebot.dbConfig.service.CommentService;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static admin.bot.adminmoviebot.bot.constants.BotState.ST_ANALYSE_OPTIONS;
import static admin.bot.adminmoviebot.bot.constants.Buttons.BTN_CHANGE_ADMIN_DETAILS;
import static admin.bot.adminmoviebot.bot.constants.Messages.MSG_LEAVE_MESSAGE_HERE;

@Service
public final class BotAnalyseService implements BotAnalyseServiceInt {

  private final UserService userService;
  private final MenuMessageSender menuMessageSender;
  private final CommentService commentService;
  private final UsersBotComponent usersBotComponent;
  private final AdminSendMessenger adminSendMessenger;
  private final ReportService reportService;
  private String commentUserId;
  private Integer messageId;

  public BotAnalyseService(UserService userService, MenuMessageSender menuMessageSender, CommentService commentService, UsersBotComponent usersBotComponent, AdminSendMessenger adminSendMessenger, ReportService reportService) {
    this.userService = userService;
    this.menuMessageSender = menuMessageSender;
    this.commentService = commentService;
    this.usersBotComponent = usersBotComponent;
    this.adminSendMessenger = adminSendMessenger;
    this.reportService = reportService;
  }

  // SEND GET COMMENTS BUTTON AND BUTTON DOWNLOAD DETAILS OF USERS
  @Override
  public SendMessage sendAnalyseDetailButton(Update update) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(Messages.MSG_CHOOSE_ONE_OF_THEM);
    String[] buttons = {Buttons.BTN_READ_COMMENTS, Buttons.BTN_DOWNLOAD_DETAILS, BTN_CHANGE_ADMIN_DETAILS, Buttons.BTN_BACK};
    ReplyKeyboardMarkup keyboardMarkup = TaskUtil.createTwoColumnKeyboard(buttons);
    sendMessage.setReplyMarkup(keyboardMarkup);
    userService.setUsersStateByUpdate(update, ST_ANALYSE_OPTIONS);
    return sendMessage;
  }

  //RESPONSE REQUESTS COMING FROM USERS FOR GET DETAILS OF BOT
  @Override
  public Object responseBotAnalyseOptions(Update update) throws IOException {
    SendMessage sendMessage ;
    String message = update.getMessage().getText();
    switch (message) {
      case Buttons.BTN_BACK -> {
        sendMessage = menuMessageSender.sendMenu(update);
        userService.setUsersStateByUpdate(update, BotState.ST_MENU);
        return sendMessage;
      }
      case Buttons.BTN_DOWNLOAD_DETAILS -> {
        userService.setUsersStateByUpdate(update,ST_ANALYSE_OPTIONS);
       return sendDocument(update);
      }
      case Buttons.BTN_READ_COMMENTS -> {
        List<SendMessage> comments = new ArrayList<>();
        List<Comment> lastFiveDaysComments = commentService.getLastTenDaysComments();
        if (lastFiveDaysComments.isEmpty()) {
          SendMessage nullComments = new SendMessage();
          nullComments.setChatId(TaskUtil.getChatIdStr(update));
          nullComments.setText(Messages.MSG_NOT_RECEIVED_COMMENTS);
          comments.add(nullComments);

          sendMessage = sendAnalyseDetailButton(update);
          comments.add(sendMessage);
          return comments;
        } else {
          lastFiveDaysComments.forEach(comment -> {
            SendMessage forwardMessage = drawCommentMessage(update, comment);
            comments.add(forwardMessage);
          });
          return comments;
        }
      }
      case BTN_CHANGE_ADMIN_DETAILS -> {
        sendMessage = adminSendMessenger.sendAdminDetails(update);
      }
      default -> {
        sendMessage = sendAnalyseDetailButton(update);
      }
    }
    return sendMessage;
  }

  // MAKE FORWARD MESSAGE COME FROM BOT USERS
  @Override
  public SendMessage drawCommentMessage(Update update, Comment comment) {
    SendMessage forwardMessage = new SendMessage();
    forwardMessage.setChatId(TaskUtil.getChatIdStr(update));
    forwardMessage.setText(STR."<b>\{comment.getCommenter().getFullName() == null ? "" : STR."""
\{comment.getCommenter().getFullName()}"""}</b>\n\n<i>\{comment.getCommentMessage()}</i>");
    forwardMessage.enableHtml(true);
    String[] buttons = {Buttons.BTN_REPLY_COMMENT};
    String[] userChatId = {STR."\{String.valueOf(comment.getCommenter().getChatId())}&&&\{comment.getMessageId()}"};
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(buttons, userChatId);
    forwardMessage.setReplyMarkup(twoColumnInlineKeyboard);
    return forwardMessage;
  }


  // SEND MESSAGE TO LEAVE MESSAGE FOR USERS
  @Override
  public SendMessage responseUsersComment(Update update) {
    String data = update.getCallbackQuery().getData();
    String[] split = data.split("&&&");
    commentUserId = split[0];
    messageId = Integer.valueOf(split[1]);
    SendMessage sendMessage = new SendMessage();
    sendMessage.setText(MSG_LEAVE_MESSAGE_HERE);

    ReplyKeyboardMarkup replyKeyboardMarkup = TaskUtil.makeBackButton();
    sendMessage.setReplyMarkup(replyKeyboardMarkup);

    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    return sendMessage;
  }


  @Override
  public void sendResponseUsersComment(Update update) throws TelegramApiException, IOException {
    if (update.hasMessage()) {
      if (update.getMessage().hasText()) {
        usersBotComponent.sendTextFormatMessage(update.getMessage().getText(), commentUserId, messageId);
      }
    }
  }

  // RETURN SENDMESSAGE WHEN HANDLE RESPONSE FROM ADMIN
  @Override
  public SendMessage onHandleReply(Update update) throws TelegramApiException, IOException {
    if (update.getMessage().getText().equals(Buttons.BTN_BACK)) {
      return sendAnalyseDetailButton(update);
    } else {
      sendResponseUsersComment(update);
      return TaskUtil.messageSender(update, Messages.MSG_MESSAGE_SENT);
    }
  }

  // SEND DOCUMENT METHOD OF REPORT
  @Override
  public SendDocument sendDocument(Update update) throws IOException {
    SendDocument sendDocument = new SendDocument();
    sendDocument.setChatId(TaskUtil.getChatIdStr(update));
    ByteArrayInputStream reportDocument = reportService.createReportDocument();
    sendDocument.setDocument(new InputFile(reportDocument,"report.xlsx"));
    return sendDocument;
  }



}
