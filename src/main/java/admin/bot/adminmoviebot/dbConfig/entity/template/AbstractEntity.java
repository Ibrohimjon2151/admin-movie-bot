package admin.bot.adminmoviebot.dbConfig.entity.template;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass

public class AbstractEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private Date creationDate;

  private Date lastUpdatedDate;



  @PrePersist
  protected void onCreate() {
    creationDate = new Date(System.currentTimeMillis());
    lastUpdatedDate = new Date(System.currentTimeMillis());
  }

  @PreUpdate
  protected void onUpdate() {
    lastUpdatedDate = new Date(System.currentTimeMillis());
  }
}
