package admin.bot.adminmoviebot.dbConfig.entity;

import admin.bot.adminmoviebot.dbConfig.entity.template.AbstractEntity;
import admin.bot.adminmoviebot.dbConfig.entity.user.bot.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends AbstractEntity {
  String comment;

  @ManyToOne
  User user;
}
