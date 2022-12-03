resource "azurerm_cognitive_account" "cog" {
  name                = "cpptesttrans"
  location            = var.location
  resource_group_name = azurerm_resource_group.rg.name
  kind                = "TextTranslation"
  sku_name            = "F0"
}

output "primarykey" {
  value = nonsensitive(azurerm_cognitive_account.cog.primary_access_key)
}
