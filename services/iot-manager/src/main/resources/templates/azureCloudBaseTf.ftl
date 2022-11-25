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

provider "azapi" {}

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

resource "azurerm_log_analytics_workspace" "law" {
  name                = "law-terraform"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "PerGB2018"
  retention_in_days   = 90
}

resource "azapi_resource" "menv" {
  type      = "Microsoft.App/managedEnvironments@2022-03-01"
  parent_id = azurerm_resource_group.rg.id
  location  = azurerm_resource_group.rg.location
  name      = "menv-terraform"

  body = jsonencode({
    properties = {
      appLogsConfiguration = {
        destination = "log-analytics"
        logAnalyticsConfiguration = {
          customerId = azurerm_log_analytics_workspace.law.workspace_id
          sharedKey  = azurerm_log_analytics_workspace.law.primary_shared_key
        }
      }
      zoneRedundant = false
    }
  })
}