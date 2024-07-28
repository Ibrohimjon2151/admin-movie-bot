package admin.bot.adminmoviebot.dbConfig.component;

import admin.bot.adminmoviebot.bot.constants.LanguageCode;
import admin.bot.adminmoviebot.dbConfig.entity.Category;
import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import admin.bot.adminmoviebot.dbConfig.entity.user.bot.AdminDetails;
import admin.bot.adminmoviebot.dbConfig.repository.AdminDetailsRepository;
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
  private final AdminDetailsRepository adminDetailsRepository;

  public DataLoader(CategoryRepository categoryRepository, MovieRepository movieRepository, AdminDetailsRepository adminDetailsRepository) {
    this.categoryRepository = categoryRepository;
    this.movieRepository = movieRepository;
   this.adminDetailsRepository = adminDetailsRepository;
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
    if (adminDetailsRepository.count() == 0) {
      AdminDetails adminDetails = new AdminDetails();
      adminDetails.setId(LanguageCode.CNS_ADMIN_ID);
      adminDetails.setFullName("Ibrokhimjon Yursunov");
      adminDetails.setPhoneNumber("+998911082151");
      adminDetails.setTgUserName("Ibrohimjon_Yursunov");
      adminDetailsRepository.save(adminDetails);
    }

  }
}
