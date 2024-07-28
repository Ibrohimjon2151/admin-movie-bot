package admin.bot.adminmoviebot.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReportData {
 public String beginDate;
 public String endDate;
 public Long newUsers;
 public Long newUsersUz;
 public Long newUsersEng;
 public Long newUsersRu;
 public Long allUsers;
 public Long allUsersUz;
 public Long allUsersEng;
 public Long allUsersRu;
}
