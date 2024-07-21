package admin.bot.adminmoviebot.dbConfig.service.user.service;

import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.entity.user.bot.User;
import admin.bot.adminmoviebot.dbConfig.payload.UserDto;
import admin.bot.adminmoviebot.dbConfig.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
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
    User user = new User(userDto.getChatId(), userDto.getBotState(), null, userDto.getLang(), userDto.getFullName(), userDto.getUserName(),null,true);
    userRepository.save(user);
  }

  //GET ACTIVE USER'S STATE BY CHATID
  public BotState getUsersStateByChatId(Long chatId) {
    Optional<User> optionalUser = userRepository.findByChatId(chatId);
    return optionalUser.filter(user -> user.getBotState() != null).map(User::getBotState).orElse(BotState.ST_MENU);
  }

  // CHANGE USR STATE BY UPDATE
  public void changeUsersStateByChatId(Long chatId, BotState newState) {
    Optional<User> byChatId = userRepository.findByChatId(chatId);
    User user = byChatId.get();
    user.setBotState(newState);
    userRepository.save(user);
  }



  // CHANGE USR STATE
  public void changeUsersStateByUpdate(Update update, BotState newState) {
    User user = getUser(update);
    user.setBotState(newState);
    userRepository.save(user);
  }

  // CHANGE PREVIOUS STATE
  public void changeUsersPreviousStateByChatId(Update update, BotState newState) {
    User user = getUser(update);
    user.setPreviousState(newState);
    userRepository.save(user);
  }

//GET USER
  private User getUser(Update update) {
    long chatId = 0L;
    if (update.hasMessage()) {
      chatId = update.getMessage().getChatId();
    } else {
      chatId = update.getCallbackQuery().getMessage().getChatId();
    }
    Optional<User> optionalUser = userRepository.findByChatId(chatId);
    User user = optionalUser.get();
    return user;
  }

  // GET PREVIOUS STATE
  public BotState getUserPreviousState(Long chatId) {
    Optional<User> optionalUser = userRepository.findByChatId(chatId);
    return optionalUser.filter(user -> user.getPreviousState() != null).map(User::getPreviousState).orElse(BotState.ST_MENU);
  }

  // SET MOVIE CODE TO USER
  public void saveMovieCode(Update update, String code) {
    Optional<User> optionalUser = userRepository.findByChatId(TaskUtil.getChatId(update));
    User user = optionalUser.get();
    user.setMovieCode(code);
    userRepository.save(user);
  }

  // GET MOVIE CODE WHICH IS CURRENTLY ACTIVE
  public String getUsersMovieCode(Update update){
    User user = getUser(update);
    return user.getMovieCode();
  }

  public List<User> getAll(){
    return userRepository.findAll();
  }

  // GET ADMIN'S ID
  public List<Long> getAdminsUserId() {
   return userRepository.findAllChatIds();
  }
}

