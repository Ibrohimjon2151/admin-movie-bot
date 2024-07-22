package admin.bot.adminmoviebot.dbConfig.repository;


import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

  boolean existsByMovieCode(String code);

  Optional<Movie> findByMovieCode(String code);

  Optional<Movie> findByPostsIsNull();

  @Query("SELECT m FROM Movie m " +
    "LEFT JOIN m.posts p " +
    "GROUP BY m " +
    "HAVING COUNT(p) = 1 OR COUNT(p) = 0")
  Movie findMoviesWithOneOrNoPosts();

  @Query("SELECT m FROM Movie m JOIN m.posts p WHERE p.originalMovie = true")
  List<Movie> findAllWithOriginalMovies();

}
