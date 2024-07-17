package admin.bot.adminmoviebot.dbConfig.repository;


import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

  boolean existsByMovieCode(String code);
}
