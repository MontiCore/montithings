<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
[common]
<#if config.getSplittingMode().toString() == "DISTRIBUTED">
DCPSBit=0
DCPSGlobalTransportConfig=myconfig

[config/myconfig]
transports=mytcp

[transport/mytcp]
transport_type=tcp

[transport/myrtpsudp]
transport_type=rtps_udp

<#else>
DCPSDefaultDiscovery=DEFAULT_RTPS
</#if>