package org.barrikeit.config;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class ServerStatusTask {

  private final DataSource dataSource;

  @Scheduled(initialDelay = 1, fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
  public void task() {
    String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    log.info("Server Status - [UP]: [{}]", currentTime);
  }

  @Scheduled(initialDelay = 1, fixedDelay = 60, timeUnit = TimeUnit.MINUTES)
  public void healthCheck() {
    checkDatabaseConnection();
    checkMemoryUsage();
    checkDiskSpace();
  }

  private void checkDatabaseConnection() {
    try (Connection connection = dataSource.getConnection()) {
      log.info("Database connection: {}", connection.isValid(2));
    } catch (SQLException e) {
      log.error("Database connection check failed: {}", e.getMessage());
    }
  }

  private void checkMemoryUsage() {
    long totalMemory = Runtime.getRuntime().totalMemory();
    long freeMemory = Runtime.getRuntime().freeMemory();
    long usedMemory = totalMemory - freeMemory;
    log.info(
        "Memory Usage - Used: {} MB, Total: {} MB",
        usedMemory / (1024 * 1024),
        totalMemory / (1024 * 1024));
  }

  private void checkDiskSpace() {
    File root = new File("/");
    long freeSpace = root.getFreeSpace();
    long totalSpace = root.getTotalSpace();
    log.info(
        "Disk Space - Free: {} MB, Total: {} MB",
        freeSpace / (1024 * 1024),
        totalSpace / (1024 * 1024));
  }
}
