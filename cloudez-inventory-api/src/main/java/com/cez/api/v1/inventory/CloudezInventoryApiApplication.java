package com.cez.api.v1.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import com.cez.api.v1.commons.tasks.InventoryReportJob;

@SpringBootApplication
public class CloudezInventoryApiApplication
{
  public static void main(String[] args)
  {
    SpringApplication.run(CloudezInventoryApiApplication.class, args);
  }

}
