package admin.bot.adminmoviebot.dbConfig.service.post.service;

import admin.bot.adminmoviebot.dbConfig.entity.Post;
import admin.bot.adminmoviebot.dbConfig.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class PostService implements PostServiceInt {

 private final PostRepository postRepository;

 public PostService(PostRepository postRepository) {
  this.postRepository = postRepository;
 }

 @Override
 public void addPost(Post post) {
  postRepository.save(post);
 }

 // GET LAST SAVED POST
 @Override
 public Post getLastPost() {
  Post lastUpdated = postRepository.getLastUpdated();
  return lastUpdated;
 }
}
