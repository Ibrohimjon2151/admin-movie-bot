package admin.bot.adminmoviebot.dbConfig.repository;


import admin.bot.adminmoviebot.dbConfig.entity.user.bot.AdminDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminDetailsRepository extends JpaRepository<AdminDetails,Long> {

}
