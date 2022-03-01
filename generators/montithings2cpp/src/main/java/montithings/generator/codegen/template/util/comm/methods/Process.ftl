<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/util/comm/helper/GeneralPreamble.ftl">


void
${className}::process (std::string msg)
{
PortToSocket message(msg);

<#list comp.getPorts() as p>
  <#assign type = TypesPrinter.getRealPortCppTypeString(comp, p, config)>

  <#if !p.isOutgoing()>
    if (message.getLocalPort() == "${p.getName()}")
    {
    // connection information for port ${p.getName()} was received
    std::string ${p.getName()}_uri = "ws://" + message.getIpAndPort() + message.getRemotePort();
    LOG(DEBUG) << "Received connection: " << ${p.getName()}_uri ;
    comp->getInterface()->addInPort${p.getName()?cap_first}(new WSPort<Message<${type}>>(INCOMING, ${p.getName()}_uri));
    }
  </#if>
</#list>
}