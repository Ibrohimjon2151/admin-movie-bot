package admin.bot.adminmoviebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;

@SpringBootApplication
public class AdminMovieBotApplication {

  public static void main(String[] args) {
    SpringApplication.run(AdminMovieBotApplication.class, args);
    System.setProperty("java.awt.headless", "false");

    // Check if headless mode is now disabled
    boolean isHeadless = GraphicsEnvironment.isHeadless();
    System.out.println(STR."Headless mode: \{isHeadless}");

  }

}
