package com.cez.api.v1.inventory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.amazonaws.arn.Arn;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.model.ListOpenIDConnectProvidersRequest;
import com.amazonaws.services.identitymanagement.model.ListRolesRequest;
import com.amazonaws.services.identitymanagement.model.ListUsersRequest;
import com.amazonaws.services.identitymanagement.model.OpenIDConnectProviderListEntry;
import com.amazonaws.services.identitymanagement.model.Role;
import com.amazonaws.services.identitymanagement.model.User;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesResult;
import com.amazonaws.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import com.amazonaws.services.s3.AmazonS3;
import com.cez.api.v1.auth.aws.Credentials;
import com.cez.api.v1.commons.aws.Client;

@Component
public class InventoryScanService
{
  @Autowired
  private AssetRepository repository;

  private Credentials credentials = null;

  private String account = "270579433622";

  private String region = "us-west-2";

  private String type = "inventory";

  @Value("${execution.mode}")
  private String executionMode;

  @Value("${reports.bucket.name}")
  private String bucketName;

  private AWSResourceGroupsTaggingAPI tagClient = null;
  private AmazonIdentityManagement iamClient = null;
  private AmazonS3 s3Client = null;

  @Scheduled(fixedDelay = 86400000, initialDelay = 3000)
  public void scan()
  {
    List<String> resourcesList = new ArrayList<>();
    credentials = new Credentials(executionMode);

    // discover all resources from tagging API
    tagClient = new Client(account, credentials).getAWSResourceGroupTaggingClient(region);
    GetResourcesRequest getResources = new GetResourcesRequest();
    String paginationToken = null;
    do
    {
      if (paginationToken != null)
      {
        getResources.setPaginationToken(paginationToken);
        getResources.setResourcesPerPage(100);
      }
      GetResourcesResult result = tagClient.getResources(getResources);
      paginationToken = result.getPaginationToken();
      for (ResourceTagMapping mapping : result.getResourceTagMappingList())
        resourcesList.add(mapping.getResourceARN());
    }
    while (paginationToken.length() > 5);

    // discover all IAM assets - they are not covered by resource tagging api
    iamClient = new Client(account, credentials).getIAMClient();
    ListUsersRequest listUserRequest = new ListUsersRequest();
    for (User user : iamClient.listUsers(listUserRequest).getUsers())
      resourcesList.add(user.getArn());

    ListRolesRequest listRolesRequest = new ListRolesRequest();
    for (Role role : iamClient.listRoles(listRolesRequest).getRoles())
      resourcesList.add(role.getArn());

    ListOpenIDConnectProvidersRequest listOIDCProviders = new ListOpenIDConnectProvidersRequest();
    for (OpenIDConnectProviderListEntry oidc : iamClient.listOpenIDConnectProviders(listOIDCProviders).getOpenIDConnectProviderList())
      resourcesList.add(oidc.getArn());

    DateTime time = new DateTime();
    String reportName =
        type + "-" + time.getYear() + time.getMonthOfYear() + time.getDayOfMonth() + "-" + time.getHourOfDay() + "HH" + time.getMinuteOfHour() + "MM" + ".csv";
    s3Client = new Client(account, credentials).getS3Client();
    String header[] = {"Account", "Service", "ID", "Type", "ARN", "Region"};

    try
    {
      File report = new File(reportName);
      FileWriter out = new FileWriter(report);
      CSVPrinter csv = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(header));

      for (String arn : resourcesList)
      {
        AWSAsset asset = new AWSAsset();
        asset.setArn(arn);
        asset.setAccount(account);
        asset.setRegion(Arn.fromString(arn).getRegion());
        asset.setUniqueIdentifier(Arn.fromString(arn).getResource().getResource());
        asset.setService(Arn.fromString(arn).getService());
        asset.setResourceQualifier(Arn.fromString(arn).getResource().getQualifier());

        if ((Arn.fromString(arn).getResource().getResourceType() == null) && (Arn.fromString(arn).getService().equalsIgnoreCase("s3")))
          asset.setResourceType("bucket");
        else
          asset.setResourceType(Arn.fromString(arn).getResource().getResourceType());

        csv.printRecord(asset.getAccount(), asset.getService(), asset.getUniqueIdentifier(), asset.getResourceType(), asset.getArn(), asset.getRegion());
        repository.save(asset);

        // push report to S3 bucket
        s3Client.putObject(bucketName, type + "/" + reportName, report).getContentMd5();
      }
      report.delete();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
