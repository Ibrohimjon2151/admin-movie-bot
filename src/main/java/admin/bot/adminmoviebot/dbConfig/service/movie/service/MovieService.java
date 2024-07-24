package admin.bot.adminmoviebot.dbConfig.service.movie.service;

import admin.bot.adminmoviebot.dbConfig.entity.Category;
import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import admin.bot.adminmoviebot.dbConfig.entity.Post;
import admin.bot.adminmoviebot.dbConfig.repository.CategoryRepository;
import admin.bot.adminmoviebot.dbConfig.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public final class MovieService implements MovieServiceInt {
  private final MovieRepository movieRepository;
  private final CategoryRepository categoryRepository;

  public MovieService(MovieRepository movieRepository, CategoryRepository categoryRepository) {
    this.movieRepository = movieRepository;
    this.categoryRepository = categoryRepository;
  }

  @Override
  public String generateUniqueCode() {
    Random random = new Random();
    String code;

    do {
      code = String.format("%05d", random.nextInt(100000)); // Generate 5-digit code
    } while (movieRepository.existsByMovieCode(code));

    return code;
  }

  @Override
  public void saveMovieCode(String code) {
    Movie movie = new Movie();
    movie.setMovieCode(code);
    movieRepository.save(movie);
  }

  @Override
  public void saveMovieName(String movieCode, String name) {
    Movie movie = getMovie(movieCode);
    movie.setName(name);
    movieRepository.save(movie);
  }

  @Override
  public void saveMovieCategoryId(String movieCode, String categoryId) {
    Movie movie = getMovie(movieCode);
    long id = Long.parseLong(categoryId);
    Optional<Category> optionalCategory = categoryRepository.findById(id);
    movie.setCategory(optionalCategory.get());
    movieRepository.save(movie);
  }

  @Override
  public void saveMovieLanguage(String movieCode, String lang) {
    Movie movie = getMovie(movieCode);
    movie.setLanguage(Integer.parseInt(lang));
    movieRepository.save(movie);
  }

  @Override
  public void saveMovieQuality(String movieCode, String data) {
    Movie movie = getMovie(movieCode);
    movie.setQuality(Integer.parseInt(data));
    movieRepository.save(movie);
  }

  @Override
  public void saveMovieSize(String movieCode, String text) {
    Movie movie = getMovie(movieCode);
    movie.setSize(text);
    movieRepository.save(movie);
  }

  @Override
  public void saveMovieRunTime(String movieCode, String text) {
    Movie movie = getMovie(movieCode);
    movie.setRunTime(text);
    movieRepository.save(movie);
  }

  @Override
  public void saveMovieYear(String movieCode, String text) {
    Movie movie = getMovie(movieCode);
    movie.setProductionYear(text);
    movieRepository.save(movie);
  }

  @Override
  public Movie getMovie(String movieCode) {
    Optional<Movie> optionalMovie = movieRepository.findByMovieCode(movieCode);
    return optionalMovie.orElseThrow(() -> new RuntimeException("Movie not found"));
  }

  // GET MOVIE WHICH POST ID IS NULL
  @Override
  public Movie getMoviePostIsNull() {
    Movie moviesWithOneOrNoPosts = movieRepository.findMoviesWithOneOrNoPosts();
    return moviesWithOneOrNoPosts;
  }

  //CONNECT MOVIE CODE AND POST ID
  @Override
  public void connectMovieAndPost(Movie moviePostIsNull, Post lastPost) {
    List<Post> posts = moviePostIsNull.getPosts();
    posts.add(lastPost);
    moviePostIsNull.setPosts(posts);
    movieRepository.save(moviePostIsNull);
  }

  @Override
  public List<Movie> getMoviesByCategoryId(Long categoryId) {
    List<Movie> byCategoryId = movieRepository.findByCategoryId(categoryId);
    return byCategoryId;
  }

  @Override
  public String getTrailersUrl(Movie movie) {
    String url = null;
    for (Post post : movie.getPosts()) {
      if (!post.isOriginalMovie()) {
        url = STR."https://t.me/\{post.getChannelUrl()}/\{post.getMessageId()}";
      }
    }
    return url;
  }

  @Override
  public void deleteMovie(String id) {
    movieRepository.deleteById(Long.valueOf(id));
  }
}
