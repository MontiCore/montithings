<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/util/comm/helper/GeneralPreamble.ftl">


void
${className}::initializePorts ()
{
// initialize ports
<#list comp.getPorts() as p>
  <#assign type = ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)>
  <#assign type = tc.includeArgs("template.logtracing.hooks.ReplaceTypeIfEnabled", [comp, config, type])>

  std::string ${p.getName()}_uri = "ws://" + comm->getOurIp() + ":" + communicationPort + "/" + comp->getInstanceName () + "/out/${p.getName()}";
  comp->getInterface()->addOutPort${p.getName()?cap_first}(new WSPort<${type}>(OUTGOING, ${p.getName()}_uri));
</#list>
}