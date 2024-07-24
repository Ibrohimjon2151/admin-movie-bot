package admin.bot.adminmoviebot.dbConfig.service.category.service;

import admin.bot.adminmoviebot.dbConfig.entity.Category;
import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import admin.bot.adminmoviebot.dbConfig.repository.CategoryRepository;
import admin.bot.adminmoviebot.dbConfig.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;

  private final MovieRepository movieRepository;

  public CategoryService(CategoryRepository categoryRepository, MovieRepository movieRepository) {
    this.categoryRepository = categoryRepository;
    this.movieRepository = movieRepository;
  }

  // GET ALL CATEGORIES
  public List<Category> getAll(){
    return categoryRepository.findAll();
  }

  public void deleteCategory(long data) {
    categoryRepository.deleteById(data);
  }

  public void saveCategory(Category category) {
    categoryRepository.save(category);
  }

  public boolean isCategoryUsed(Long id){
    List<Movie> byCategoryId = movieRepository.findByCategoryId(id);
    return !byCategoryId.isEmpty();
  }

  public String[] getAllCategoriesName() {
    String[] array = getAll().stream().map(Category::getName)
     .toArray(String[]::new);
    return array;
  }

  public String[] getAllCategoriesIds() {
    String[] ids = getAll().stream()
     .map(category -> String.valueOf(category.getId()))
     .toArray(String[]::new);
    return ids;
  }
}
