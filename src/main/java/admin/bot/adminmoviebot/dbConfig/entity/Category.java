package admin.bot.adminmoviebot.dbConfig.entity;

import admin.bot.adminmoviebot.dbConfig.entity.template.AbstractEntity;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class Category extends AbstractEntity {

  private String name;

  private String langCode;
}
