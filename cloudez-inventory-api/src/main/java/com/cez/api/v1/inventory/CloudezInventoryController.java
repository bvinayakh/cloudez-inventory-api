package com.cez.api.v1.inventory;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
import com.cez.api.v1.aws.auth.Credentials;
import com.cez.api.v1.commons.aws.Client;

@RestController
@RequestMapping("inventory/api/v1")
public class CloudezInventoryController
{
  @Autowired
  private AssetRepository repository;

  private Credentials credentials = null;

  private AWSResourceGroupsTaggingAPI tagClient = null;
  private AmazonIdentityManagement iamClient = null;

  @GetMapping("/ping")
  String ping()
  {
    return ("cloudez-inventory-api");
  }

  @GetMapping("/scan/{account}/{region}")
  // @ExceptionHandler({AuthExceptionResolver.class})
  Integer scan(@PathVariable String account, @PathVariable String region)
  {
    int totalAssetsDiscovered = 0;
    List<String> resourcesList = new ArrayList<>();
    credentials = new Credentials();

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
    {
      resourcesList.add(user.getArn());
      totalAssetsDiscovered++;
    }

    ListRolesRequest listRolesRequest = new ListRolesRequest();
    for (Role role : iamClient.listRoles(listRolesRequest).getRoles())
    {
      resourcesList.add(role.getArn());
      totalAssetsDiscovered++;
    }

    ListOpenIDConnectProvidersRequest listOIDCProviders = new ListOpenIDConnectProvidersRequest();
    for (OpenIDConnectProviderListEntry oidc : iamClient.listOpenIDConnectProviders(listOIDCProviders).getOpenIDConnectProviderList())
    {
      resourcesList.add(oidc.getArn());
      totalAssetsDiscovered++;
    }


    for (String arn : resourcesList)
    {
      // System.out.println(Arn.fromString(mapping.getResourceARN()).getRegion());
      // System.out.println(Arn.fromString(mapping.getResourceARN()).getService());
      // System.out.println(Arn.fromString(mapping.getResourceARN()).getResourceAsString());
      // System.out.println(Arn.fromString(mapping.getResourceARN()).getResource().getResourceType());
      // System.out.println(Arn.fromString(mapping.getResourceARN()).getResource().getResource());
      // System.out.println(Arn.fromString(mapping.getResourceARN()).getResource().getQualifier());

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

      totalAssetsDiscovered++;
      repository.save(asset);

    }
    return totalAssetsDiscovered;
  }
}
