package com.cez.api.v1.inventory;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "inventory")
public class AWSAsset
{
  private @Column(name = "resource_identifier", nullable = false) String resourceId;
  private @Column(name = "account", nullable = false) String account;
  private @Column(name = "resource_type", nullable = false) String resourceType;
  private @Column(name = "state", nullable = true) String state;
  private @Id @Column(name = "arn", nullable = false) String arn;
  private @Column(name = "region", nullable = true) String region;
  private @Column(name = "resource_qualifier", nullable = true) String resourceQualifier;
  private @Column(name = "service", nullable = true) String service;

  // getters
  public String getUniqueIdentifier()
  {
    return resourceId;
  }

  public String getAccount()
  {
    return account;
  }

  public String getResourceType()
  {
    return resourceType;
  }

  public String getState()
  {
    return state;
  }

  public String getArn()
  {
    return arn;
  }

  public String getRegion()
  {
    return region;
  }

  public String getResourceQualifier()
  {
    return resourceQualifier;
  }

  public String getService()
  {
    return service;
  }

  // setters
  public void setUniqueIdentifier(String uniqueIdentifier)
  {
    this.resourceId = uniqueIdentifier;
  }

  public void setAccount(String account)
  {
    this.account = account;
  }

  public void setResourceType(String resourceType)
  {
    this.resourceType = resourceType;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public void setArn(String arn)
  {
    this.arn = arn;
  }

  public void setRegion(String region)
  {
    this.region = region;
  }

  public void setResourceQualifier(String resourceQualifier)
  {
    this.resourceQualifier = resourceQualifier;
  }

  public void setService(String service)
  {
    this.service = service;
  }

  // defaults
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (!(obj instanceof AWSAsset)) return false;
    AWSAsset asset = (AWSAsset) obj;
    return Objects.equals(this.resourceId, asset.resourceId) && Objects.equals(this.account, asset.account);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(this.account, this.resourceId);
  }

  @Override
  public String toString()
  {
    return this.arn;
  }

}
