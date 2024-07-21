package admin.bot.adminmoviebot.bot.messengers.channel;

import admin.bot.adminmoviebot.bot.component.ChannelConfigComponent;
import admin.bot.adminmoviebot.bot.constants.BotState;
import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.Messages;
import admin.bot.adminmoviebot.bot.messengers.util.TaskUtil;
import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import admin.bot.adminmoviebot.dbConfig.entity.Post;
import admin.bot.adminmoviebot.dbConfig.entity.user.bot.User;
import admin.bot.adminmoviebot.dbConfig.repository.MovieRepository;
import admin.bot.adminmoviebot.dbConfig.repository.PostRepository;
import admin.bot.adminmoviebot.dbConfig.service.movie.service.MovieService;
import admin.bot.adminmoviebot.dbConfig.service.post.service.PostService;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

@Service
public final class ChannelMessengers implements ChannelMessengersInt {

 private final ChannelConfigComponent channelConfigComponent;
 private final MovieRepository movieRepository;
 private final PostRepository postRepository;
 private final MovieService movieService;
 private final UserService userService;
 private final PostService postService;

 public ChannelMessengers(ChannelConfigComponent channelConfigComponent, MovieRepository movieRepository, PostRepository postRepository, MovieService movieService, UserService userService, PostService postService) {
  this.channelConfigComponent = channelConfigComponent;
  this.movieRepository = movieRepository;
  this.postRepository = postRepository;
  this.movieService = movieService;
  this.userService = userService;
  this.postService = postService;
 }

 @Override
 public List<SendMessage> handlePostFromChannel(Update update) {
  List<SendMessage> messages = new ArrayList<>();
  List<Long> adminsUserId = userService.getAdminsUserId();
  Message message = update.getChannelPost();
  String userName = message.getChat().getUserName();

  // DECLARE POST AND SAVE POST
  Post post = new Post();
  post.setChatId(message.getChatId());
  post.setMessageId(message.getMessageId());

  if (userName != null) {
   post.setOriginalMovie(false);
  }
  postService.addPost(post);

  Movie moviePostIsNull = movieService.getMoviePostIsNull();
  for (Long l : adminsUserId) {
   SendMessage sendMessage = new SendMessage();
   sendMessage.enableHtml(true);
   if (moviePostIsNull == null && userName != null && userName.equals(channelConfigComponent.getTRAILER_CHANNEL_USERNAME())) {
    sendMessage.setChatId(String.valueOf(l));
    sendMessage.setText(Messages.MSG_FORWARD_BOT_USERS);
    String[] buttons = {Buttons.BTN_YES, Buttons.BTN_NO};
    String[] buttonsData = {Buttons.DATA_PERMIT_FORWARD, Buttons.DATA_BANNED_FORWARD};
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(buttons, buttonsData);
    sendMessage.setReplyMarkup(twoColumnInlineKeyboard);
    userService.changeUsersStateByChatId(l, BotState.ST_DO_FORWARD);
   }else if (userName != null && userName.equals(channelConfigComponent.getTRAILER_CHANNEL_USERNAME())){
    sendMessage.setChatId(String.valueOf(l));
    sendMessage.setText(STR."<b>\{moviePostIsNull.getMovieCode()}</b>\{Messages.MSG_CONNECT_TRAILER}");
    String[] buttons = {Buttons.BTN_YES, Buttons.BTN_NO,Buttons.BTN_FORWARD_BOT_USERS,Buttons.BTN_CONNECT_TRAILER};
    String[] buttonsData = {Buttons.DATA_PERMIT_FORWARD, Buttons.DATA_BANNED_FORWARD,Buttons.BTN_FORWARD_BOT_USERS, Buttons.BTN_CONNECT_TRAILER};
    InlineKeyboardMarkup twoColumnInlineKeyboard = TaskUtil.createTwoColumnInlineKeyboard(buttons, buttonsData);
    sendMessage.setReplyMarkup(twoColumnInlineKeyboard);
    userService.changeUsersStateByChatId(l, BotState.ST_CONNECT_TRAILER);
   }
   messages.add(sendMessage);
  }
  return messages;
 }


 // FORWARD MESSAGE FORM CHANNEL TO ADMINS
 @Override
 public List<ForwardMessage> forwardMessageToAdmins(Update update) {
  List<ForwardMessage> forwardMessages = new ArrayList<>();
  List<Long> adminsUserId = userService.getAdminsUserId();
  Message message = update.getChannelPost();
  String userName = message.getChat().getUserName();
  for (Long l : adminsUserId) {
   ForwardMessage forwardMessage = new ForwardMessage();
   if (userName != null && userName.equals(channelConfigComponent.getTRAILER_CHANNEL_USERNAME())) {
    forwardMessage.setFromChatId(String.valueOf(message.getChatId()));
    forwardMessage.setChatId(String.valueOf(l));
    forwardMessage.setMessageId(message.getMessageId());
   }
   forwardMessages.add(forwardMessage);
  }

  return forwardMessages;
 }


 // FORWARD PREVIOUS MESSAGE TO ALL BOT USERS
 @Override
 public List<ForwardMessage> forwardSelectedMessageToUsers() {
  List<ForwardMessage> forwardMessages = new ArrayList<>();

  Post lastPost = postService.getLastPost();
  List<User> all = userService.getAll();
  for (User user : all) {
   ForwardMessage forwardMessage = new ForwardMessage();
   forwardMessage.setFromChatId(String.valueOf(lastPost.getChatId()));
   forwardMessage.setMessageId(lastPost.getMessageId());
   forwardMessage.setChatId(String.valueOf(user.getChatId()));
   forwardMessages.add(forwardMessage);
  }
  return forwardMessages;
 }
}
