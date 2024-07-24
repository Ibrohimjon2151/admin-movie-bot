package admin.bot.adminmoviebot.dbConfig.service.movie.service;

import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import admin.bot.adminmoviebot.dbConfig.entity.Post;

import java.util.List;

public sealed interface MovieServiceInt permits MovieService {
  String generateUniqueCode();

  void saveMovieCode(String code);

  void saveMovieName(String movieCode, String name);

  void saveMovieCategoryId(String movieCode, String categoryId);

  void saveMovieLanguage(String movieCode, String lang);

  void saveMovieQuality(String movieCode, String data);

  void saveMovieSize(String movieCode, String text);

  void saveMovieRunTime(String movieCode, String text);

  void saveMovieYear(String currentMovieCode, String text);

  Movie getMovie(String movieCode);

  Movie getMoviePostIsNull();

  void connectMovieAndPost(Movie moviePostIsNull, Post lastPost);

 List<Movie> getMoviesByCategoryId(Long categoryId);
}
