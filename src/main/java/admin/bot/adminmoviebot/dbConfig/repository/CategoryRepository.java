package admin.bot.adminmoviebot.dbConfig.repository;

import admin.bot.adminmoviebot.dbConfig.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
