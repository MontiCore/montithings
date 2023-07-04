<#-- (c) https://github.com/MontiCore/monticore -->
terraform {
  required_providers {
    azurerm = {
      source = "hashicorp/azurerm"
    }
    grafana = {
      source  = "grafana/grafana"
      version = "1.24.0"
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