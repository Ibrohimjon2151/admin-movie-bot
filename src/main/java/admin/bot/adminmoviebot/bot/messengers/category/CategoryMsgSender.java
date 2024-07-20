package admin.bot.adminmoviebot.bot.messengers.category;

import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.menu.MenuMessageSender;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.entity.Category;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import admin.bot.adminmoviebot.dbConfig.service.category.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static admin.bot.adminmoviebot.bot.constants.BotState.ST_MENU;

@Service
@Slf4j
public final class CategoryMsgSender implements CategoryMsgSenderInt {

  private final CategoryService categoryService;
  private final MenuMessageSender menuMessageSender;
  private final UserService userService;

  public CategoryMsgSender(CategoryService categoryService, MenuMessageSender menuMessageSender, UserService userService) {
    this.categoryService = categoryService;
    this.menuMessageSender = menuMessageSender;
    this.userService = userService;
  }

  @Override
  public SendMessage sendAllCategoriesList(Update update) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(Messages.MSG_CATEGORIES_LIST);
    List<Category> all = categoryService.getAll();
    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

    // Create the list of keyboard rows
    List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
    for (Category category : all) {
      List<InlineKeyboardButton> row = getInlineKeyboardButtons(category);
      // Add row to the list of keyboard rows
      keyboardRows.add(row);
    }
    List<InlineKeyboardButton> row = new ArrayList<>();

    InlineKeyboardButton plusButton = new InlineKeyboardButton();
    plusButton.setText(Buttons.BTN_PLUS);
    plusButton.setCallbackData(Buttons.BTN_PLUS);

    InlineKeyboardButton backButton = new InlineKeyboardButton();
    backButton.setText(Buttons.BTN_BACK);
    backButton.setCallbackData(Buttons.BTN_BACK);

    row.add(plusButton);
    row.add(backButton);

    keyboardRows.add(row);
    inlineKeyboardMarkup.setKeyboard(keyboardRows);

    sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    return sendMessage;
  }

  private static List<InlineKeyboardButton> getInlineKeyboardButtons(Category category) {
    List<InlineKeyboardButton> row = new ArrayList<>();

    // Create the name button
    InlineKeyboardButton nameButton = new InlineKeyboardButton();
    nameButton.setText(category.getName());
    nameButton.setCallbackData(category.getName());

    // Create the ID button
    InlineKeyboardButton idButton = new InlineKeyboardButton();
    idButton.setText(Buttons.BTN_MINUS);
    idButton.setCallbackData(String.valueOf(category.getId()));

    // Add buttons to the row
    row.add(nameButton);
    row.add(idButton);
    return row;
  }

  @Override
  public SendMessage deleteOrAddCategory(Update update) {
    SendMessage sendMessage = new SendMessage();
    String data = update.getCallbackQuery().getData();
    if (Buttons.BTN_PLUS.equals(data)) {
      sendMessage = addNewCategory(update);
      userService.changeUsersStateByChatId(update, BotState.ST_ENTER_NEW_CATEGORY_NAME);
    } else if (Buttons.BTN_BACK.equals(data)) {
      userService.changeUsersStateByChatId(update, ST_MENU);
      sendMessage = menuMessageSender.sendMenu(update);
    } else {
      if (isConvertibleToLong(data)) {
        categoryService.deleteCategory(Long.parseLong(data));
      }
      sendMessage = sendAllCategoriesList(update);
    }
    return sendMessage;
  }

  @Override
  public SendMessage addNewCategory(Update update) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(TaskUtil.getChatIdStr(update));
    sendMessage.setText(Messages.MSG_ENTER_CATEGORY_NAME);
    return sendMessage;
  }

  public static boolean isConvertibleToLong(String str) {
    try {
      Long.parseLong(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  @Override
  public void saveCategoryName(Update update) {
    String name = update.getMessage().getText();
    Category category = new Category();
    category.setName(name);
    categoryService.saveCategory(category);
  }
}
