<#-- (c) https://github.com/MontiCore/monticore -->
terraform {
  required_providers {
    azurerm = {
      source = "hashicorp/azurerm"
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