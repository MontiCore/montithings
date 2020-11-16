<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#list comp.getPorts() as p>
    <#if p.isIncoming()>
      this->addInPort${p.getName()?cap_first}
    <#else>
      this->addOutPort${p.getName()?cap_first}
    </#if>
  (new MqttPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(this->getInstanceName () + "/${p.getName()}"));
</#list>