package admin.bot.adminmoviebot.dbConfig.entity;

import admin.bot.adminmoviebot.bot.constants.Language;
import admin.bot.adminmoviebot.dbConfig.entity.template.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie extends AbstractEntity {

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String movieCode;

  private float size;

  @ManyToOne
  private Category category;

  @Enumerated(EnumType.STRING)
  @Column(name = "language_code", nullable = false)
  private Language language;

  private int quality;


}
