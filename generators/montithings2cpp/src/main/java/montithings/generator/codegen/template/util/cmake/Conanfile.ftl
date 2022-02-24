<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "existsHWC")}

<#assign needsMosquitto = brokerIsMQTT>
<#assign needsNng = !targetPlatformIsDsaVcg
                 && !targetPlatformIsDsaLab
                 && !splittingModeDisabled
                 && brokerDisabled>

[requires]
<#if needsMosquitto>
mosquitto/2.0.10
</#if>
<#if needsNng>
nng/1.3.0
</#if>

[generators]
cmake