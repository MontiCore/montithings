<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

<#list comp.getAstNode().getConnectors() as connector>
  <#list connector.getTargetList() as target>
    // implements "${connector.getSource().getQName()} -> ${target.getQName()}"
    // implements "source.value -> sink.value"
    connectorPortOut->sendToExternal(comp->getInstanceName() + ".${connector.getSource().getQName()}/out->" + comp->getInstanceName() + ".${target.getQName()}/in");

    LOG(DEBUG) << "Published connector via DDS: " << comp->getInstanceName() + ".${connector.getSource().getQName()}/out->" + comp->getInstanceName() + ".${target.getQName()}/in";
  </#list>
</#list>