<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#list comp.getPorts() as p>
    <#if p.isOutgoing()>
      // outgoing port ${p.getName()}
      comp->addOutPort${p.getName()?cap_first}(new DDSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(*this, OUTGOING, comp->getInstanceName() + ".value/out"));
    </#if>
</#list>