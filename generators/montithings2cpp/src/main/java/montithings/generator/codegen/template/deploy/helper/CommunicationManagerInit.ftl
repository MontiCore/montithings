<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "OFF">
  ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name}Manager manager (&cmp, managementPortArg.getValue(), dataPortArg.getValue());
  manager.initializePorts ();
  <#if comp.isDecomposed()>
    manager.searchSubcomponents ();
  </#if>
</#if>