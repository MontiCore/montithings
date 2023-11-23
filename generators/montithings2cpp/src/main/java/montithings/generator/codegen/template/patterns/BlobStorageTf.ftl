<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("containername")}
// Storage account provisioned in Deployment Manager
// Name of the resource is sa
resource "azurerm_storage_container" "${containername}" {
  name                  = "${containername}"
  storage_account_name  = azurerm_storage_account.sa.name
  container_access_type = "private"
}

// Obtain shared access signature for file upload container
data "azurerm_storage_account_blob_container_sas" "sas" {
  connection_string = azurerm_storage_account.sa.primary_connection_string
  container_name    = azurerm_storage_container.${containername}.name
  https_only        = false

  start  = "2023-01-01"
  expiry = "2025-01-01"

  permissions {
    read   = true
    add    = true
    create = true
    write  = false
    delete = false
    list   = true
  }
}

output "containerSasUrl" {
  value = data.azurerm_storage_account_blob_container_sas.sas.sas
}