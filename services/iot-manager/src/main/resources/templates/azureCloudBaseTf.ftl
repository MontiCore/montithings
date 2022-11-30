<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("storageAccountName")}

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

variable "location" {
  type        = string
  default     = "germanywestcentral"
  description = "Desired Azure Region"
}

resource "azurerm_resource_group" "rg" {
  name     = "rg-terraform"
  location = var.location
}

resource "azurerm_storage_account" "sa" {
  name                     = "${storageAccountName}"
  resource_group_name      = azurerm_resource_group.rg.name
  location                 = var.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}