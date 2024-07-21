package admin.bot.adminmoviebot.dbConfig.service.post.service;

import admin.bot.adminmoviebot.dbConfig.entity.Post;

public interface PostServiceInt {
 void addPost(Post post);

 Post getLastPost();
}
