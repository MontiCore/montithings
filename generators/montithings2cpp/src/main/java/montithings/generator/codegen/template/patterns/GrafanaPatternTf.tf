terraform {
   required_providers {
      grafana = {
         source  = "grafana/grafana"
         version = "1.24.0"
      }
   }
}

provider "grafana" {
   alias = "cloud"

   url   = "<Grafana-instance-url>"
   auth  = "<Grafana-API-Key>"
}

// TODO Generate postgres db

resource "grafana_data_source" "postgres" {
  type                = "postgres"
  name                = "montithingspostgres"
  url                 = "${postgresurl}"
  username            = "${username}"
  password            = "${password}"
  database_name       = "${databasename}"
}

resource "grafana_dashboard" "azure_monitor_storage_insights" {
  provider    = grafana.base
  config_json = "${tc.includeArgs("template.patterns.GrafanaDashboard", [comp, config, className])}"
}