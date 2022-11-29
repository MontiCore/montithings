resource "azurerm_storage_account" "sa" {
  name                     = "tfstorageaccount139473"
  resource_group_name      = "rg-terraform"
  location                 = "germanywestcentral"
  account_tier             = "Standard"
  account_replication_type = "LRS"
}
