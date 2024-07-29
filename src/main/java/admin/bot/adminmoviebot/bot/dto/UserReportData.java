package admin.bot.adminmoviebot.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReportData {
 public Date beginDate;
 public Date endDate;
 public long newUsers;
 public long newUsersUz;
 public long newUsersEng;
 public long newUsersRu;
 public long allUsers;
 public long allUsersUz;
 public long allUsersEng;
 public long allUsersRu;
}
