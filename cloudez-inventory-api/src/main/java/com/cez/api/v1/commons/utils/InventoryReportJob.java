package com.cez.api.v1.commons.utils;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class InventoryReportJob implements Job
{

  @Override
  public void execute(JobExecutionContext arg0) throws JobExecutionException
  {
    System.out.println("Inventory Report Scanning Job");
  }

}
