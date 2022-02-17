<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("modules", "deploymentInfo", "networkInfo")}
{
"modulesContent": {
"$edgeAgent": {
"properties.desired": {
"modules": {
<#list modules as moduleName>
    "${moduleName?replace(".", "-")}": {
    "settings": {
    "image": "${networkInfo.getDockerRepositoryPrefix()}${deploymentInfo.getInstanceInfo(moduleName).getComponentType()?lower_case}:latest",
    "createOptions": "{\"Entrypoint\":[\"sh\",\"entrypoint.sh\",\"-n\", \"${moduleName}\", \"--brokerHostname\", \"${networkInfo.getMqttHost()}\", \"--brokerPort\", \"${networkInfo.getMqttPort()?c}\"],\"NetworkingConfig\": {\"EndpointsConfig\": {\"host\": {}}},\"HostConfig\": {\"NetworkMode\": \"host\"}}"
    },
    "type": "docker",
    "status": "running",
    "restartPolicy": "always",
    "version": "1.0"
    }<#sep>,
</#list>
},
"runtime": {
"settings": {
"minDockerVersion": "v1.25",
"registryCredentials": {
"myRegistry": {
"address": "${networkInfo.getDockerRepositoryPrefix()?keep_before("/")}",
"password": "${networkInfo.getDockerRepositoryPassword()}",
"username": "${networkInfo.getDockerRepositoryUsername()}"
}
}
},
"type": "docker"
},
"schemaVersion": "1.1",
"systemModules": {
"edgeAgent": {
"settings": {
"image": "mcr.microsoft.com/azureiotedge-agent:1.1",
"createOptions": ""
},
"type": "docker"
},
"edgeHub": {
"type": "docker",
"status": "running",
"restartPolicy": "always",
"settings": {
"image": "mcr.microsoft.com/azureiotedge-hub:1.2",
"createOptions": "{\"HostConfig\":{\"PortBindings\":{\"5671/tcp\":[{\"HostPort\":\"5671\"}],\"8883/tcp\":[{\"HostPort\":\"8883\"}],\"443/tcp\":[{\"HostPort\":\"443\"}],\"1883/tcp\":[{\"HostPort\":\"1883\"}]}}}"
},
"env": {
"experimentalFeatures__mqttBrokerEnabled": {
"value": "true"
},
"experimentalFeatures__enabled": {
"value": "true"
},
"RuntimeLogLevel": {
"value": "debug"
}
}
}
}
}
},
"$edgeHub": {
"properties.desired": {
"routes": {
"route": "FROM /messages/* INTO $upstream"
},
"schemaVersion": "1.1",
"storeAndForwardConfiguration": {
"timeToLiveSecs": 7200
}
}
}
}
}