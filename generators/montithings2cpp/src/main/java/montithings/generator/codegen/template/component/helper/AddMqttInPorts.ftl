<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#list comp.getIncomingPorts() as p>
  <#assign type = ComponentHelper.getRealPortCppTypeString(comp, p, config)>
  <#assign type = tc.includeArgs("template.logtracing.hooks.ReplaceTypeIfEnabled", [comp, config, type])>

  // incoming port ${p.getName()}
  MqttPort<${type}> *${p.getName()} = new MqttPort<${type}>(this->getInstanceName () + "/${p.getName()}");
  interface.getPort${p.getName()?cap_first} ()->attach (this);
  this->interface.addInPort${p.getName()?cap_first} (${p.getName()});

  <#if !comp.isAtomic()>
    // additional outgoing port for port incoming port ${p.getName()}
    // to forward data to subcomponents
    this->interface.addOutPort${p.getName()?cap_first}(new MqttPort<${type}>(this->getInstanceName () + "/${p.getName()}", false));
  </#if>
</#list>
