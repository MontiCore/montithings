<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#list comp.getPorts() as p>
    <#if p.isIncoming()>
      // incoming port ${p.getName()}
      this->addInPort${p.getName()?cap_first}
    <#else>
      // outgoing port ${p.getName()}
      this->addOutPort${p.getName()?cap_first}
    </#if>
  (new MqttPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(this->getInstanceName () + "/${p.getName()}"));
    <#if p.isIncoming() && !comp.isAtomic()>
      // additional outgoing port for port incoming port ${p.getName()}
      // to forward data to subcomponents
      this->addOutPort${p.getName()?cap_first}(new MqttPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(this->getInstanceName () + "/${p.getName()}"));
    </#if>

</#list>