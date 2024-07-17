package admin.bot.adminmoviebot.dbConfig.service;

import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.dbConfig.entity.userBot.User;
import admin.bot.adminmoviebot.dbConfig.payload.UserDto;
import admin.bot.adminmoviebot.dbConfig.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;


@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  //CHECK USER EXIST IN DATABASE
  public boolean isExistsByChatId(Long chatId) {
    return userRepository.findByChatId(chatId).isPresent();
  }

  //SAVE USER FIRST TIME CLICK START BUTTON
  public void saveUser(UserDto userDto) {
    User user = new User(userDto.getChatId(), userDto.getBotState(), userDto.getLang(), userDto.getFullName(), userDto.getUserName());
    userRepository.save(user);
  }

  //GET ACTIVE USER'S STATE BY CHATID
  public BotState getUsersStateByChatId(Long chatId) {
    Optional<User> optionalUser = userRepository.findByChatId(chatId);
    return optionalUser.filter(user -> user.getBotState() != null).map(User::getBotState).orElse(BotState.ST_MENU);
  }

  public void changeUsersStateByChatId(Update update, BotState newState) {
    long chatId = 0L;
    if (update.hasMessage()) {
      chatId = update.getMessage().getChatId();
    } else {
      chatId = update.getCallbackQuery().getMessage().getChatId();
    }
    Optional<User> optionalUser = userRepository.findByChatId(chatId);
    User user = optionalUser.get();
    user.setBotState(newState);
    userRepository.save(user);
  }
}

