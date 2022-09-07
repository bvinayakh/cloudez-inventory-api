package com.cez.api.v1.auth.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class Credentials implements AWSCredentialsProvider
{
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
