package com.cez.api.v1.auth.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class Credentials implements AWSCredentialsProvider
{
  public static final Logger logger = LoggerFactory.getLogger(Credentials.class);

  @SuppressWarnings("unused")
  private String account = null;

  private String executionMode = null;

  private AWSCredentials credentials = null;

  public Credentials(String mode)
  {
    this.executionMode = mode;
  }

  @Override
  public AWSCredentials getCredentials()
  {
    if (executionMode.equalsIgnoreCase("local")) credentials = new ProfileCredentialsProvider().getCredentials();
    else if (executionMode.equalsIgnoreCase("apiserver")) credentials = WebIdentityTokenCredentialsProvider.builder().build().getCredentials();

    logger.info("Using " + executionMode + " credentials");

    return credentials;
  }

  public void setAccount(String account)
  {
    this.account = account;
  }

  @Override
  public void refresh()
  {}
}
