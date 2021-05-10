<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/util/comm/helper/GeneralPreamble.ftl">


void
${className}::initializePorts ()
{
// initialize ports
<#list comp.getPorts() as p>
  <#assign type = ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)>

  std::string ${p.getName()}_uri = "ws://" + comm->getOurIp() + ":" + communicationPort + "/" + comp->getInstanceName () + "/out/${p.getName()}";
  comp->getInterface()->addOutPort${p.getName()?cap_first}(new WSPort<Message<${type}>>(OUTGOING, ${p.getName()}_uri));
</#list>
}