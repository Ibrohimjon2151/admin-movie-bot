package admin.bot.adminmoviebot.bot.messengers.analyse;

import admin.bot.adminmoviebot.bot.constants.Buttons;
import admin.bot.adminmoviebot.bot.constants.ExcelVariables;
import admin.bot.adminmoviebot.bot.dto.UserReportData;
import admin.bot.adminmoviebot.dbConfig.payload.LanguageCountDto;
import admin.bot.adminmoviebot.dbConfig.service.user.service.UserService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ReportService {

 private final UserService userService;

 public ReportService(UserService userService) {
  this.userService = userService;
 }


 public ByteArrayInputStream createReportDocument() throws IOException {

  Date earliesDate = userService.getFirstCreatedUser();
  Calendar earliestCalendar = Calendar.getInstance();
  earliestCalendar.setTime(earliesDate);
  int earliestYear = earliestCalendar.get(Calendar.YEAR);

  // Create a Calendar instance
  Calendar currentCalendar = Calendar.getInstance();
  int currentYear = currentCalendar.get(Calendar.YEAR);
  Date beginDate;
  Date endDate;

  Calendar calendar = Calendar.getInstance();

  List<UserReportData> userReportData = new ArrayList<>();

  for (int i = earliestYear; i <= currentYear; i++) {
   if (i < currentYear) {
    calendar.set(Calendar.YEAR, i);
    calendar.set(Calendar.MONTH, Calendar.JANUARY);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    beginDate = calendar.getTime();

    calendar.set(Calendar.YEAR, i);
    calendar.set(Calendar.MONTH, Calendar.DECEMBER);
    calendar.set(Calendar.DAY_OF_MONTH, 31);
    endDate = calendar.getTime();

    UserReportData data = makeReportData(beginDate, endDate);
    userReportData.add(data);

   } else {
    int earliestMonth = (earliestCalendar.get(Calendar.MONTH) + 1);
    int currentMonth = (currentCalendar.get(Calendar.MONTH) + 1);
    for (int j = earliestMonth; j <= currentMonth; j++) {
     calendar.set(Calendar.YEAR, i);
     calendar.set(Calendar.MONTH, j);
     calendar.set(Calendar.DAY_OF_MONTH, 1);
     beginDate = calendar.getTime();

     calendar.set(Calendar.YEAR, i);
     calendar.set(Calendar.MONTH, j);
     calendar.set(Calendar.DAY_OF_MONTH, j == 1 ? 28 : 30);
     endDate = calendar.getTime();
     UserReportData data = makeReportData(beginDate, endDate);
     userReportData.add(data);
    }
   }
  }

  return makeUsersReport(userReportData);
 }

 private UserReportData makeReportData(Date beginDate, Date endDate) {
  UserReportData data = new UserReportData();
  data.setBeginDate(String.valueOf(beginDate));
  data.setEndDate(String.valueOf(endDate));
  long newUsers = 0;
  for (LanguageCountDto languageCountDto : userService.languageCountDto(beginDate, endDate)) {
   newUsers += languageCountDto.getCount();
   if (languageCountDto.getLangCode() == 0) {
    data.setNewUsersUz(languageCountDto.getCount());
   } else if (languageCountDto.getLangCode() == 1) {
    data.setNewUsersEng(languageCountDto.getCount());
   } else {
    data.setNewUsersRu(languageCountDto.getCount());
   }
  }
  data.setNewUsers(newUsers);

  newUsers = 0;
  for (LanguageCountDto languageCountDto : userService.getUsersLangCountUntilTheDate(endDate)) {
   newUsers += languageCountDto.getCount();
   if (languageCountDto.getLangCode() == 0) {
    data.setAllUsersUz(languageCountDto.getCount());
   } else if (languageCountDto.getLangCode() == 1) {
    data.setAllUsersEng(languageCountDto.getCount());
   } else {
    data.setAllUsersRu(languageCountDto.getCount());
   }
  }
  data.setAllUsers(newUsers);

  return data;
 }

 //RECEIVE DATA AND DRAW IT TO REPORT
 public static ByteArrayInputStream makeUsersReport(List<UserReportData> dataList) throws IOException {
  Workbook workbook = new XSSFWorkbook();
  Sheet sheet = workbook.createSheet("Sheet with Custom Column Width");

  int rowNum = 0;
  for (int i = 0; i < dataList.size(); i++) {

   Row row = sheet.createRow(rowNum);
   Cell cell1 = row.createCell(8);

   CellStyle cellStyle = workbook.createCellStyle();
   cellStyle.setAlignment(HorizontalAlignment.CENTER);
   Font boldTitle = workbook.createFont();
   boldTitle.setBold(true);
   cellStyle.setFont(boldTitle);
   cell1.setCellStyle(cellStyle);
   cell1.setCellValue(STR."\{dataList.get(i).getBeginDate()} - \{dataList.get(i).getEndDate()}");

   Row rowHeader = sheet.createRow(rowNum+5);

   String [] headers = {ExcelVariables.PR_NEW_USERS,ExcelVariables.PR_NEW_USER_UZB,ExcelVariables.PR_NEW_USER_ENG,ExcelVariables.PR_NEW_USER_RU,ExcelVariables.PR_ALL_USERS, ExcelVariables.PR_ALL_USERS_UZ, ExcelVariables.PR_ALL_USERS_ENG, ExcelVariables.PR_ALL_USERS_RU};
   for (int k = 0; k < headers.length; k++) {
    Cell cell = rowHeader.createCell(k);
    sheet.addMergedRegion(new CellRangeAddress(rowNum+5, rowNum+5, k, k+3));
    CellStyle cellStyleHeader = workbook.createCellStyle();
    cellStyleHeader.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
    cellStyleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    cellStyleHeader.setAlignment(HorizontalAlignment.CENTER);
    cell.setCellStyle(cellStyle);
    cell.setCellValue(headers[k]);
   }

   rowNum+=5;
  }
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  workbook.write(outputStream);
  workbook.close();
  byte[] excelBytes = outputStream.toByteArray();

  return new ByteArrayInputStream(excelBytes);
 }


}
