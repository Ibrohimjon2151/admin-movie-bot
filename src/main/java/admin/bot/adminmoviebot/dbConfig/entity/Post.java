package admin.bot.adminmoviebot.dbConfig.entity;

import admin.bot.adminmoviebot.dbConfig.entity.template.AbstractEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Post extends AbstractEntity {

  private String channelUrl;

  private Long messageId;
}
