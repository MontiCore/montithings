<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("storageAccountName")}
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