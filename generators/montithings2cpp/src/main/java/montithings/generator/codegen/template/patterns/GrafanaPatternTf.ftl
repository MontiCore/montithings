<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grafanaInstanceUrl", "grafanaApiKey", "panels", "title", "setupProvider")}
<#if setupProvider == true>
provider "grafana" {
   alias = "cloud"
   url   = "${grafanaInstanceUrl}"
   auth  = "${grafanaApiKey}"
}

resource "azurerm_postgresql_flexible_server" "smallpgdb" {
  name                   = "montithingssmallpgdb"
  resource_group_name    = azurerm_resource_group.rg.name
  location               = azurerm_resource_group.rg.location
  version                = "14"
  administrator_login    = "postgres"
  administrator_password = "secretpgpassword1234"
  backup_retention_days  = "7"
  zone                   = "1"
  storage_mb             = 32768
  sku_name               = "B_Standard_B1ms"
}

resource "azurerm_postgresql_flexible_server_firewall_rule" "smallpgdbfirewall" {
  name             = "montithingssmallpgdb-fw"
  server_id        = azurerm_postgresql_flexible_server.smallpgdb.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "255.255.255.255"
}

resource "grafana_data_source" "grafanapg" {
  type                = "postgres"
  uuid                = "eOSdhkbVk1234"
  name                = "montithingspostgres"
  url                 = azurerm_postgresql_flexible_server.smallpgdb.fqdn
  username            = azurerm_postgresql_flexible_server.smallpgdb.administrator_login
  password            = azurerm_postgresql_flexible_server.smallpgdb.administrator_password
  database_name       = "postgres"
}
</#if>

resource "grafana_dashboard" "grafanapgdb" {
  provider    = grafana.base
  config_json = "${tc.includeArgs("template.patterns.GrafanaDashboard", [panels, title])}"
}