terraform {
  required_providers {
    azurerm = {
      source = "hashicorp/azurerm"
    }
    azapi = {
      source = "Azure/azapi"
    }
  }
}

provider "azurerm" {
  features {}
}

provider "azapi" {}

resource "azurerm_resource_group" "rg" {
  name     = "rg-terraform"
  location = var.location
}

resource "azurerm_container_group" "example" {
  name                = "example-continst"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  ip_address_type     = "Public"
  dns_name_label      = "aci-label"
  os_type             = "Linux"

  container {
    name     = "hierarchyexample"
    image    = "montithings.azurecr.io/hierarchy.example:latest"
    cpu      = "0.5"
    memory   = "1.5"
    commands = ["sh", "entrypoint.sh", "--name", "hierarchy.example"]

    environment_variables = {
      "key" = "val"
    }

    ports {
      port     = 443
      protocol = "TCP"
    }
  }

  image_registry_credential = {
    server   = "montithings.azurecr.io"
    username = "montithings"
    password = "2mf/PjWWBVdk60IletHL9XWiYtPTq1Bq"
  }
}
