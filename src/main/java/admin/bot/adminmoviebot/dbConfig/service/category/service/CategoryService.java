package admin.bot.adminmoviebot.dbConfig.service.category.service;

import admin.bot.adminmoviebot.dbConfig.entity.Category;
import admin.bot.adminmoviebot.dbConfig.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
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
}
