<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
[common]
<#if splittingModeIsDistributed>
DCPSBit=0
DCPSGlobalTransportConfig=myconfig

[config/myconfig]
transports=mytcp

[transport/mytcp]
transport_type=tcp

[transport/myrtpsudp]
transport_type=rtps_udp

<#else>
DCPSGlobalTransportConfig=$file
DCPSDefaultDiscovery=DEFAULT_RTPS

[transport/the_rtps_transport]
transport_type=rtps_udp
</#if>