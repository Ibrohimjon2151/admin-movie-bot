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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        int currentMonth = (currentCalendar.get(Calendar.MONTH));
        for (int j = 0; j <= currentMonth; j++) {
          calendar.set(Calendar.YEAR, i);
          calendar.set(Calendar.MONTH, j);
          calendar.set(Calendar.DAY_OF_MONTH, 1);
          beginDate = calendar.getTime();

          calendar.set(Calendar.YEAR, i);
          calendar.set(Calendar.MONTH, j);
          calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
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
    data.setBeginDate(beginDate);
    data.setEndDate(endDate);

    for (LanguageCountDto languageCountDto : userService.languageCountDto(beginDate, endDate)) {
      if (languageCountDto.getLangCode() == 0) {
        data.setNewUsersUz(languageCountDto.getCount());
      } else if (languageCountDto.getLangCode() == 1) {
        data.setNewUsersEng(languageCountDto.getCount());
      } else {
        data.setNewUsersRu(languageCountDto.getCount());
      }
    }
    int newUsers = userService.getUsersCountBetweenDates(beginDate, endDate);
    data.setNewUsers(newUsers);

    for (LanguageCountDto languageCountDto : userService.getUsersLangCountUntilTheDate(endDate)) {
      if (languageCountDto.getLangCode() == 0) {
        data.setAllUsersUz(languageCountDto.getCount());
      } else if (languageCountDto.getLangCode() == 1) {
        data.setAllUsersEng(languageCountDto.getCount());
      } else {
        data.setAllUsersRu(languageCountDto.getCount());
      }
    }
    int allUsersCount = userService.getUsersCount(endDate);
    data.setAllUsers(allUsersCount);

    return data;
  }

  //RECEIVE DATA AND DRAW IT TO REPORT
  public static ByteArrayInputStream makeUsersReport(List<UserReportData> dataList) throws IOException {
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Sheet with Custom Column Width");
    Font font = workbook.createFont();
    font.setBold(true);
    font.setFontHeightInPoints((short) 14);
    CellStyle cellDatesStyle = workbook.createCellStyle();
    cellDatesStyle.setAlignment(HorizontalAlignment.CENTER);
    cellDatesStyle.setFont(font);

    int rowIndex = 3;
    for (UserReportData data : dataList) {
      Row rowDates = sheet.createRow(rowIndex);
      Cell datesCell = rowDates.createCell(0);
      datesCell.setCellStyle(cellDatesStyle);
      String pattern = "dd.MM.yyyy";
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

      datesCell.setCellValue(STR."\{simpleDateFormat.format(data.getBeginDate())} - \{simpleDateFormat.format(data.getEndDate())}");
      sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 13));


      String[] headers = {
        ExcelVariables.PR_NEW_USERS,
        ExcelVariables.PR_NEW_USER_UZB,
        ExcelVariables.PR_NEW_USER_ENG,
        ExcelVariables.PR_NEW_USER_RU,
        ExcelVariables.PR_ALL_USERS,
        ExcelVariables.PR_ALL_USERS_UZ,
        ExcelVariables.PR_ALL_USERS_ENG,
        ExcelVariables.PR_ALL_USERS_RU
      };

      int rowHeaderInd = rowIndex + 2;
      Row rowHeader = sheet.createRow(rowHeaderInd);
      CellStyle headerStyle = workbook.createCellStyle();
      CellStyle headerStyleSpec = workbook.createCellStyle();

      headerStyle.setAlignment(HorizontalAlignment.CENTER);
      headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
      headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      headerStyleSpec.setAlignment(HorizontalAlignment.CENTER);
      headerStyleSpec.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
      headerStyleSpec.setFillPattern(FillPatternType.SOLID_FOREGROUND);

      Cell cell = rowHeader.createCell(0);
      cell.setCellStyle(headerStyleSpec);
      cell.setCellValue(headers[0]);
      sheet.setColumnWidth(0, 20 * 256);

      int colInd = 1;
      for (int i = 0; i < 3; i++) {
        sheet.addMergedRegion(new CellRangeAddress(rowHeaderInd, rowHeaderInd, colInd, colInd + 1));
        cell = rowHeader.createCell(colInd);
        cell.setCellValue(headers[i + 1]);
        cell.setCellStyle(headerStyle);
        sheet.setColumnWidth(colInd, 14 * 256);
        sheet.setColumnWidth(colInd + 1, 14 * 256);
        colInd += 2;
      }

      cell = rowHeader.createCell(7);
      cell.setCellStyle(headerStyleSpec);
      cell.setCellValue(headers[4]);
      sheet.setColumnWidth(7, 20 * 256);

      colInd = 8;
      for (int i = 5; i < 8; i++) {
        sheet.addMergedRegion(new CellRangeAddress(rowHeaderInd, rowHeaderInd, colInd, colInd + 1));
        cell = rowHeader.createCell(colInd);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
        sheet.setColumnWidth(colInd, 14 * 256);
        sheet.setColumnWidth(colInd + 1, 14 * 256);
        colInd += 2;
      }

      Row values = sheet.createRow(rowHeaderInd + 1);
      Cell cell1 = values.createCell(0);
      cell1.setCellValue(data.getNewUsers());

      Cell cell2 = values.createCell(1);
      cell2.setCellValue(data.getNewUsersUz());

      Cell cell3 = values.createCell(2);
      cell3.setCellValue(STR."\{(Math.round((double) data.getNewUsersUz() / data.getNewUsers() * 100))}%");

      Cell cell4 = values.createCell(3);
      cell4.setCellValue(data.getNewUsersEng());

      Cell cell5 = values.createCell(4);
      cell5.setCellValue(STR."\{(Math.round((double) data.getNewUsersEng() / data.getNewUsers() * 100))}%");

      Cell cell6 = values.createCell(5);
      cell6.setCellValue(data.getNewUsersRu());

      Cell cell7 = values.createCell(6);
      cell7.setCellValue(STR."\{(Math.round((double) data.getNewUsersRu() / data.getNewUsers() * 100))}%");

      Cell cell8 = values.createCell(7);
      cell8.setCellValue(data.getAllUsers());

      Cell cell9 = values.createCell(8);
      cell9.setCellValue(data.getAllUsersUz());

      Cell cell10 = values.createCell(9);
      cell10.setCellValue(STR."\{(Math.round((double) data.getAllUsersUz() / data.getAllUsers() * 100))}%");

      Cell cell11 = values.createCell(10);
      cell11.setCellValue(data.getAllUsersEng());

      Cell cell12 = values.createCell(11);
      cell12.setCellValue(STR."\{(Math.round((double) data.getAllUsersEng() / data.getAllUsers() * 100))}%");

      Cell cell13 = values.createCell(12);
      cell13.setCellValue(data.getAllUsersRu());

      Cell cell14 = values.createCell(13);
      cell14.setCellValue(STR."\{(Math.round((double) data.getAllUsersRu() / data.getAllUsers() * 100))}%");

      CellStyle cellStyle = workbook.createCellStyle();
      cellStyle.setAlignment(HorizontalAlignment.CENTER);
      for (Cell value : values) {
        value.setCellStyle(cellStyle);
      }

      rowIndex += 5;
    }
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    workbook.write(outputStream);
    workbook.close();
    byte[] excelBytes = outputStream.toByteArray();

    return new ByteArrayInputStream(excelBytes);
  }


}
