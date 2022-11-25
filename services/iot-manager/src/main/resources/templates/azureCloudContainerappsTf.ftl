<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("modules", "deploymentInfo", "networkInfo")}

<#list modules as moduleName>
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
}<#sep>
</#list>