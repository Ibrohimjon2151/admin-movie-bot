package admin.bot.adminmoviebot.dbConfig.service;

import admin.bot.adminmoviebot.bot.constants.Language;
import admin.bot.adminmoviebot.dbConfig.entity.Category;
import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import admin.bot.adminmoviebot.dbConfig.repository.CategoryRepository;
import admin.bot.adminmoviebot.dbConfig.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class MovieService {
  private final MovieRepository movieRepository;
  private final CategoryRepository categoryRepository;

  public MovieService(MovieRepository movieRepository, CategoryRepository categoryRepository) {
    this.movieRepository = movieRepository;
    this.categoryRepository = categoryRepository;
  }

  public String generateUniqueCode() {
    Random random = new Random();
    String code;

    do {
      code = String.format("%05d", random.nextInt(100000)); // Generate 5-digit code
    } while (movieRepository.existsByMovieCode(code));

    return code;
  }


  //SAVE MOVIE'S MOVIE CODE
  public void saveMovieCode(String code) {
    Movie movie = new Movie();
    movie.setMovieCode(code);
    movieRepository.save(movie);
  }

  //SAVE MOVIE'S NAME BY MOVIE CODE
  public void saveMovieName(String movieCode, String name) {
    Optional<Movie> optionalMovie = movieRepository.findByMovieCode(movieCode);
    Movie movie = optionalMovie.get();
    movie.setName(name);
    movieRepository.save(movie);
  }

  //SAVE MOVIE'S NAME BY MOVIE CODE
  public void saveMovieCategoryId(String movieCode, String categoryId) {
    Optional<Movie> optionalMovie = movieRepository.findByMovieCode(movieCode);
    Movie movie = optionalMovie.get();
    long id = Long.parseLong(categoryId);
    Optional<Category> optionalCategory = categoryRepository.findById(id);
    movie.setCategory(optionalCategory.get());
    movieRepository.save(movie);
  }

  //SAVE MOVIE LANG
  public void saveMovieLanguage(String movieCode, String lang) {
    Optional<Movie> optionalMovie = movieRepository.findByMovieCode(movieCode);
    Movie movie = optionalMovie.get();
    movie.setLanguage(Language.valueOf(lang));
    movieRepository.save(movie);
  }

  //SAVE MOVIE QUALITY
  public void saveMovieQuality(String movieCode, String data) {
    Optional<Movie> optionalMovie = movieRepository.findByMovieCode(movieCode);
    Movie movie = optionalMovie.get();
    movie.setQuality(Integer.parseInt(data));
    movieRepository.save(movie);
  }
}
