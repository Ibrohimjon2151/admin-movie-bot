package admin.bot.adminmoviebot.dbConfig.component;

import admin.bot.adminmoviebot.dbConfig.entity.Category;
import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import admin.bot.adminmoviebot.dbConfig.repository.CategoryRepository;
import admin.bot.adminmoviebot.dbConfig.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
  private final CategoryRepository categoryRepository;
  private final MovieRepository movieRepository;

  public DataLoader(CategoryRepository categoryRepository, MovieRepository movieRepository) {
    this.categoryRepository = categoryRepository;
    this.movieRepository = movieRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    if (categoryRepository.count() == 0) {
      String [] categories = {"Hujjatli film\uD83D\uDCC3","Ilmiy kino\uD83E\uDDD0","Romantika😍"};
      List<Category> categoryList  = new ArrayList<>();
      var index = 0;
      for (String name : categories) {
        Category category = new Category();
        category.setName(name);
        category.setLangCode(String.valueOf(index));
        categoryList.add(category);
        index++;
      }
      categoryRepository.saveAll(categoryList);
      Movie movie = new Movie();
      movie.setMovieCode("31121");
      movie.setName("vsdvsdvsdvsd");
      movie.setLanguage(0);
      movie.setCategory(categoryList.get(0));
      movie.setRunTime("1212");
      movieRepository.save(movie);
    }
  }
}
