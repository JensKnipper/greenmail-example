package de.jensknipper.greenmailexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GreenmailExampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(GreenmailExampleApplication.class, args);
  }
}
