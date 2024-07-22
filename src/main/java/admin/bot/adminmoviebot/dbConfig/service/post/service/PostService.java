package admin.bot.adminmoviebot.dbConfig.service.post.service;

import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import admin.bot.adminmoviebot.dbConfig.entity.Post;
import admin.bot.adminmoviebot.dbConfig.repository.MovieRepository;
import admin.bot.adminmoviebot.dbConfig.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService implements PostServiceInt {

 private final PostRepository postRepository;
 private final MovieRepository movieRepository;

 public PostService(PostRepository postRepository, MovieRepository movieRepository) {
  this.postRepository = postRepository;
   this.movieRepository = movieRepository;
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


 //IS EXIST MOVIE THAT ADDED TRAILER
 @Override
 public boolean existMovieTrailerAdded(Movie movie) {
  List<Post> posts = movie.getPosts();
  int count = 0;
  for (Post post : posts) {
   if (!post.isOriginalMovie()) {
    count++;
   }
  }
  return count==0;
 }
}
