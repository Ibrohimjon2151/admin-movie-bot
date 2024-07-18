package admin.bot.adminmoviebot.dbConfig.component;

import admin.bot.adminmoviebot.dbConfig.entity.Category;
import admin.bot.adminmoviebot.dbConfig.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
  private final CategoryRepository categoryRepository;

  public DataLoader(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    if (categoryRepository.count() == 0) {
      String [] categories = {"Hujjatli film\uD83D\uDCC3","Ilmiy kino\uD83E\uDDD0","Romantika😍"};
      List<Category> categoryList  = new ArrayList<>();
      for (String name : categories) {
        Category category = new Category();
        category.setName(name);
        categoryList.add(category);
      }
      categoryRepository.saveAll(categoryList);
    }
  }
}
