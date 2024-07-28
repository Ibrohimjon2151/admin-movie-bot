package admin.bot.adminmoviebot.dbConfig.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LanguageCountDto {
 private int langCode; // 0-uzb, 1-eng, 2-ru
 private Long count;
}
