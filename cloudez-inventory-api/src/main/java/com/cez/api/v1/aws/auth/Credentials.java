package com.cez.api.v1.aws.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class Credentials implements AWSCredentialsProvider
{
  private static final Logger logger = LoggerFactory.getLogger(Credentials.class);
  private String account = null;

  private AWSCredentials credentials = null;

  public Credentials()
  {}

  @Override
  public AWSCredentials getCredentials()
  {
    // get profile credentials
    credentials = new ProfileCredentialsProvider().getCredentials();
    // if null then get eks container credentials
    if (credentials == null)
    {
      credentials = WebIdentityTokenCredentialsProvider.builder().build().getCredentials();
      logger.debug("using container credentials");
    }
    else
      logger.debug("using profile credentials");

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
