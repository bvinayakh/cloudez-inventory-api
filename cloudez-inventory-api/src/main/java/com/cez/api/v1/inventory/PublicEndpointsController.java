package com.cez.api.v1.inventory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("inventory/public")
public class PublicEndpointsController
{
  @GetMapping("/ping")
  String ping()
  {
    return ("cloudez-inventory-api");
  }
}
