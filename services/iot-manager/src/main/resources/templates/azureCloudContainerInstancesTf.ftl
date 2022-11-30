<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("modules", "deploymentInfo", "networkInfo")}

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

    ports {
      port     = 8${moduleName?index}
      protocol = "TCP"
    }
  }<#sep>
  </#list>
}


resource "azapi_resource" "ca${moduleName?replace(".", "")?replace("_", "")?lower_case}" {
  type      = "Microsoft.App/containerApps@2022-03-01"
  parent_id = azurerm_resource_group.rg.id
  location  = azurerm_resource_group.rg.location
  name      = "ca${moduleName?replace(".", "")?replace("_", "")?lower_case}"

  body = jsonencode({
    properties : {
      managedEnvironmentId = azapi_resource.menv.id
      configuration = {
        ingress = {
          external   = true
          targetPort = 4230
        }
        registries = [
          {
            identity          = ""
            passwordSecretRef = "registrypwd"
            server            = "${networkInfo.getDockerRepositoryPrefix()?keep_before("/")}"
            username          = "${networkInfo.getDockerRepositoryUsername()}"
          }
        ]
        secrets = [
          {
            name  = "registrypwd"
            value = "${networkInfo.getDockerRepositoryPassword()}"
          }
        ]
      }
      template = {
        containers = [
          {
            name  = "ca${moduleName?replace(".", "")?replace("_", "")?lower_case}"
            image = "${networkInfo.getDockerRepositoryPrefix()}${deploymentInfo.getInstanceInfo(moduleName).getComponentType()?lower_case}:latest"
            resources = {
              cpu    = 0.25
              memory = "0.5Gi"
            }
            env = []
          }
        ]
        scale = {
          minReplicas = 0
          maxReplicas = 10
        }
      }
    }
  })
}