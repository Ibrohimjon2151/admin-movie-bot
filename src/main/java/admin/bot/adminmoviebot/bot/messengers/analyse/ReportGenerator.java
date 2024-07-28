package admin.bot.adminmoviebot.bot.messengers.analyse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;

import admin.bot.adminmoviebot.bot.constants.ExcelVariables;
import admin.bot.adminmoviebot.bot.dto.UserReportData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportGenerator {
 public static void main(String[] args) throws IOException {
  saveUsersReport(null);
 }

 public static void saveUsersReport(List<UserReportData> dataList) throws IOException {
  Workbook workbook = new XSSFWorkbook();
  Sheet sheet = workbook.createSheet("Sheet with Custom Column Width");

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

  Row rowHeader = sheet.createRow(5);

  Cell cell = rowHeader.createCell(1);
  cell.setCellValue(headers[0]);
  sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 1));

  cell = rowHeader.createCell(3);
  cell.setCellValue(headers[1]);
  sheet.addMergedRegion(new CellRangeAddress(5, 5, 2, 3));


  // Specify the path to save the file
  String filePath = Paths.get("src", "main", "resources", "users_report.xlsx").toString();
  try (OutputStream outputStream = new FileOutputStream(filePath)) {
   workbook.write(outputStream);
  }

  workbook.close();
 }
}
