<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "existsHWC")}

<#assign needsMosquitto = config.getMessageBroker().toString() == "MQTT">
<#assign needsNng = config.getTargetPlatform().toString() != "DSA_VCG"
                 && config.getTargetPlatform().toString() != "DSA_LAB"
                 && !(config.getSplittingMode().toString() == "OFF")
                 && config.getMessageBroker().toString() == "OFF">

[requires]
<#if needsMosquitto>
mosquitto/2.0.10
</#if>
<#if needsNng>
nng/1.3.0
</#if>

[generators]
cmake