<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
// initialize ports
<#list comp.getPorts() as p>
  <#assign type = ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)>
  std::string ${p.getName()}_uri = "ws://" + comm->getOurIp() + ":" + communicationPort + "/" + comp->getInstanceName () + "/out/${p.getName()}";
  comp->addOutPort${p.getName()?cap_first}(new WSPort<${type}>(OUTGOING, ${p.getName()}_uri));
</#list>