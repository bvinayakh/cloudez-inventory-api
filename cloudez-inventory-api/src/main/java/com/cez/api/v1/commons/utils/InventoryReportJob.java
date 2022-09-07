package com.cez.api.v1.commons.utils;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class InventoryReportJob extends QuartzJobBean
{

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException
  {
    System.out.println("Inventory Scanning Job");
  }

}
