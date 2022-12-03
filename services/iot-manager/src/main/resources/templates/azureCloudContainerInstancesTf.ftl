<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("modules", "deploymentInfo", "networkInfo", "envvars")}

resource "azurerm_container_group" "montithingsci" {
  name                = "montithingsci"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  ip_address_type     = "Public"
  dns_name_label      = "montithingsci"
  os_type             = "Linux"
  
  image_registry_credential {
    server   = "${networkInfo.getDockerRepositoryPrefix()?keep_before("/")}"
    username = "${networkInfo.getDockerRepositoryUsername()}"
    password = "${networkInfo.getDockerRepositoryPassword()}"
  }

  <#list modules as moduleName>
  container {
    name     = "${moduleName?replace(".", "")?replace("_", "")?lower_case}"
    image    = "${networkInfo.getDockerRepositoryPrefix()}${deploymentInfo.getInstanceInfo(moduleName).getComponentType()?lower_case}:latest"
    cpu      = "0.5"
    memory   = "1.5"
    commands = [\"sh\",\"entrypoint.sh\",\"-n\", \"${moduleName}\", \"--brokerHostname\", \"${networkInfo.getMqttHost()}\", \"--brokerPort\", \"${networkInfo.getMqttPort()?c}\", \"--localBrokerPort\", \"4230\"]
    
    environment_variables = {
      <#list envvars?keys as prop>
        "${prop}" = "${envvars.get(prop)}"
      </#list>
    }

    ports {
      port     = 8${moduleName?index}
      protocol = "TCP"
    }
  }<#sep>
  </#list>
}