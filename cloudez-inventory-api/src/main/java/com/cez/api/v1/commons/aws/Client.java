package com.cez.api.v1.commons.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPIClient;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPIClientBuilder;
import com.cez.api.v1.auth.aws.Credentials;

public class Client
{
  public static final Logger logger = LoggerFactory.getLogger(Client.class);

  private Credentials credentials = null;

  public Client(String account, Credentials credentials)
  {
    credentials.setAccount(account);
    this.credentials = credentials;
  }

  public AWSResourceGroupsTaggingAPI getAWSResourceGroupTaggingClient(String... region)
  {
    AWSResourceGroupsTaggingAPIClientBuilder client = AWSResourceGroupsTaggingAPIClient.builder();
    if (region.length > 0) client.setRegion(region[0]);
    client.setCredentials(credentials);
    return client.build();
  }

  public AmazonIdentityManagement getIAMClient()
  {
    AmazonIdentityManagementClientBuilder client = AmazonIdentityManagementClient.builder();
    client.setCredentials(credentials);
    return client.build();
  }
}
