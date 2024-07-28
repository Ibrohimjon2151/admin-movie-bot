package admin.bot.adminmoviebot.dbConfig.entity;

import admin.bot.adminmoviebot.dbConfig.entity.template.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie extends AbstractEntity {

  private String name;

  @Column(nullable = false)
  private String movieCode;

  @ManyToOne
  private Category category;

  private int language;

  private int quality;

  private String runTime;

  private String size;

  private String productionYear;

  @OneToMany
  private List<Post> posts;

  private int likeCount;
}
