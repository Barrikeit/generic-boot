package org.barrikeit.config;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class ServerStatusScheduler {

  @Bean
  public ThreadPoolTaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(1);
    scheduler.setThreadFactory(new CustomThreadFactory("Server Status"));
    return scheduler;
  }

  private static class CustomThreadFactory implements ThreadFactory {
    private final String namePrefix;
    private final AtomicInteger counter = new AtomicInteger(0);

    public CustomThreadFactory(String namePrefix) {
      this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
      return new Thread(r, namePrefix + "-" + counter.incrementAndGet());
    }
  }
}
