package admin.bot.adminmoviebot.dbConfig.service;

import admin.bot.adminmoviebot.dbConfig.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MovieService {
  private final MovieRepository movieRepository;

  public MovieService(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  public String generateUniqueCode() {
    Random random = new Random();
    String code;

    do {
      code = String.format("%05d", random.nextInt(100000)); // Generate 5-digit code
    } while (movieRepository.existsByMovieCode(code));

    return code;
  }
}
