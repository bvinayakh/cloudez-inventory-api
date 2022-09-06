package com.cez.api.v1.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class CloudezInventoryApiApplication
{
  public static void main(String[] args)
  {
    SpringApplication.run(CloudezInventoryApiApplication.class, args);
  }

}
