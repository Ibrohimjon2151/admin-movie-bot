package admin.bot.adminmoviebot.bot.messengers.admin;

import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.LanguageCode;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.analyse.BotAnalyseService;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.entity.user.bot.AdminDetails;
import admin.bot.adminmoviebot.dbConfig.repository.AdminDetailsRepository;
import admin.bot.adminmoviebot.dbConfig.service.admin.AdminService;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static admin.bot.adminmoviebot.bot.constants.BotState.*;
import static admin.bot.adminmoviebot.bot.constants.Messages.*;

@Service
public final class AdminSendMessenger implements AdminSendMessengerInt {
 private final AdminDetailsRepository adminDetailsRepository;
 private final UserService userService;
 private final AdminService adminService;

 public AdminSendMessenger(AdminDetailsRepository adminDetailsRepository, UserService userService, AdminService adminService) {
  this.adminDetailsRepository = adminDetailsRepository;
  this.userService = userService;
  this.adminService = adminService;
 }

 @Override
 public SendMessage sendAdminDetails(Update update) {
  SendMessage message = new SendMessage();
  message.setText(Messages.MSG_SELECT_WANTED_FIELD);
  message.setChatId(TaskUtil.getChatIdStr(update));
  String adminDetailsString = getAdminDetailsString();
  message.setText(adminDetailsString);
  message.enableHtml(true);
  String[] buttons = {Buttons.BTN_ADMIN_FULL_NAME, Buttons.BTN_ADMIN_USERNAME, Buttons.BTN_PHONE_NUMBER, Buttons.BTN_BACK};
  InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(buttons, buttons);
  message.setReplyMarkup(twoColumnInlineKeyboard);
  userService.setUsersStateByUpdate(update, BotState.ST_HANDLE_SELECTED_FIELD);
  return message;
 }

 public String getAdminDetailsString() {
  Optional<AdminDetails> optionalAdminDetails = adminDetailsRepository.findById(LanguageCode.CNS_ADMIN_ID);
  AdminDetails adminDetails = optionalAdminDetails.get();

  StringBuilder message = new StringBuilder();
  message.append(MSG_ADMIN_INFO)
   .append("\n\n")
   .append("<b>")
   .append(MSG_ADMIN_FULL_NAME)
   .append("</b>")
   .append("<i>")
   .append(STR." \{adminDetails.getFullName()}")
   .append("</i>\n")
   .append("<b>")
   .append(MSG_ADMIN_USERNAME)
   .append("</b>")
   .append(" ")
   .append(adminDetails.getPhoneNumber())
   .append("\n")
   .append("<b>")
   .append(MSG_ADMIN_PHONE_NUMBER)
   .append("</b>")
   .append(" " + "@")
   .append(adminDetails.getTgUserName())
   .append("\n");
  return String.valueOf(message);
 }


 @Override
 public SendMessage sendFieldsMessage(Update update) {
  String data = update.getCallbackQuery().getData();
  userService.setUsersStateByUpdate(update, ST_HANDLE_ADMIN_DETAILS);
  switch (data) {
   case Buttons.BTN_ADMIN_FULL_NAME -> {
    userService.setUsersPreviousStateByChatId(update, BotState.ST_HANDLE_NEW_ADMIN_NAME);
    return TaskUtil.messageSender(update, Messages.MSG_ENTER_ADMIN_NAME);
   }
   case Buttons.BTN_ADMIN_USERNAME -> {
    userService.setUsersPreviousStateByChatId(update, ST_HANDLE_NEW_ADMIN_USERNAME);
    return TaskUtil.messageSender(update, Messages.MSG_ENTER_ADMIN_USERNAME);
   }
   case Buttons.BTN_PHONE_NUMBER -> {
    userService.setUsersPreviousStateByChatId(update, ST_HANDLE_NEW_ADMIN_PHONE_NUMBER);
    return TaskUtil.messageSender(update, Messages.MSG_ENTER_ADMIN_PHONE_NUMBER);
   }
  }
  userService.setUsersPreviousStateByChatId(update,null);
  return null;
 }

 @Override
 public SendMessage handleNewAdminField(Update update) {
  BotState previousState = userService.getUserPreviousState(TaskUtil.getChatId(update));
  String text = update.getMessage().getText();
  switch (previousState) {
   case ST_HANDLE_NEW_ADMIN_NAME -> {
    adminService.editFullName(text);
    userService.setUsersStateByUpdate(update, BotState.ST_HANDLE_SELECTED_FIELD);
    return sendAdminDetails(update);
   }case ST_HANDLE_NEW_ADMIN_USERNAME -> {
    adminService.editUsername(text);
    userService.setUsersStateByUpdate(update, BotState.ST_HANDLE_SELECTED_FIELD);
    return sendAdminDetails(update);
   }
   case ST_HANDLE_NEW_ADMIN_PHONE_NUMBER -> {
    adminService.editPhoneNumber(text);
    userService.setUsersStateByUpdate(update, BotState.ST_HANDLE_SELECTED_FIELD);
    return sendAdminDetails(update);
   }
  }
  return null;
 }

}
