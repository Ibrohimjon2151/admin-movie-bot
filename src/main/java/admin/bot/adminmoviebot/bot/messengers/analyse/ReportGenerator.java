package admin.bot.adminmoviebot.bot.messengers.analyse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import admin.bot.adminmoviebot.bot.constants.ExcelVariables;
import admin.bot.adminmoviebot.bot.dto.UserReportData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportGenerator {
  public static void main(String[] args) throws ParseException {
    String pattern = "dd.MM.yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String parse = simpleDateFormat.format(new Date(System.currentTimeMillis()));
    System.out.println(parse);
  }
}
