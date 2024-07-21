package admin.bot.adminmoviebot.dbConfig.service.movie.service;

import admin.bot.adminmoviebot.dbConfig.entity.Movie;

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
}
