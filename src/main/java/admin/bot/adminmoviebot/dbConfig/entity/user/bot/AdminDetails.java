package admin.bot.adminmoviebot.dbConfig.entity.user.bot;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminDetails {

  @Id
  private Long id;

  private String fullName;

  private String tgUserName;

  private String phoneNumber;

}
