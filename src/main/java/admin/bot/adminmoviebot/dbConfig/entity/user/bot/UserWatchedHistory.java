package admin.bot.adminmoviebot.dbConfig.entity.user.bot;

import admin.bot.adminmoviebot.dbConfig.entity.template.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "user_watched_history")
public class UserWatchedHistory extends AbstractEntity {

 @OneToOne
 private User user;

 @OneToMany
 private List<UserMovies> userMovies;

}
