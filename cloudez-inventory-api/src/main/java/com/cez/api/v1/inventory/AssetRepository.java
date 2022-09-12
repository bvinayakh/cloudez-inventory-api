package com.cez.api.v1.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface AssetRepository extends JpaRepository<AWSAsset, String>
{
}
