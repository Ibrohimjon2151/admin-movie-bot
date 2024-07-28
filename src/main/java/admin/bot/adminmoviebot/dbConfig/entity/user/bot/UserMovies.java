package admin.bot.adminmoviebot.dbConfig.entity.user.bot;

import admin.bot.adminmoviebot.dbConfig.entity.Movie;
import admin.bot.adminmoviebot.dbConfig.entity.template.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "user_watched_movies")
public class UserMovies extends AbstractEntity {

 @OneToOne
 private Movie movie;

 private boolean evaluated = false;

}
