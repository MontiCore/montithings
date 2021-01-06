<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "OFF">
  ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name}Manager manager (&cmp, argv[2], argv[3]);
  manager.initializePorts ();
  <#if comp.isDecomposed()>
    manager.searchSubcomponents ();
  </#if>
</#if>