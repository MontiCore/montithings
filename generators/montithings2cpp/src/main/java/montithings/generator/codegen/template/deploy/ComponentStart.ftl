<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

${tc.includeArgs("template.deploy.MqttInit", [comp, config])}

<#-- NO TEMPLATE ARGUMENTS -->
<#if config.getTypeArguments(comp)?size == 0>

  ${tc.includeArgs("template.deploy.DDSParticipantInit", [comp, config])}
  ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name} cmp (
  instanceNameArg.getValue ()
  <#if comp.getParameters()?size gt 0>,</#if>
  <#list comp.getParameters() as variable>
      ${variable.getName()} <#sep>,</#sep>
  </#list>
  );
  ${tc.includeArgs("template.deploy.DDSParticipantSetCmp", [comp, config])}
  ${tc.includeArgs("template.deploy.CommunicationManagerInit", [comp, config])}

  <#list ComponentHelper.getSIUnitPortNames(comp) as portName>
    cmp.setPort${portName?cap_first}ConversionFactor(${portName}ConversionFactor);
  </#list>

  cmp.setUp(
  <#if ComponentHelper.isTimesync(comp)>
    TIMESYNC
  <#else>
    EVENTBASED
  </#if>
  );
  cmp.init();
  <#if !ComponentHelper.isTimesync(comp)>
    cmp.start();
  </#if>
  ${tc.includeArgs("template.deploy.KeepAlive", [comp, config])}

<#else>
  <#-- WITH TEMPLATE ARGUMENTS -->
  <#list config.getTypeArguments(comp) as typeArguments>
    ${tc.includeArgs("template.deploy.DDSParticipantInit", [comp, config])}
    <#if typeArguments?counter gt 1>else</#if>
    if (_typeArgs == "${typeArguments}")
    {
      ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name}${"<"}${typeArguments}${">"} cmp (
      instanceNameArg.getValue ()
      <#if comp.getParameters()?size gt 0>,</#if>
      <#list comp.getParameters() as variable>
        ${variable.getName()} <#sep>,</#sep>
      </#list>
    );
    ${tc.includeArgs("template.deploy.DDSParticipantSetCmp", [comp, config])}
    ${tc.includeArgs("template.deploy.CommunicationManagerInit", [comp, config])}

    cmp.setUp(
    <#if ComponentHelper.isTimesync(comp)>
      TIMESYNC
    <#else>
      EVENTBASED
    </#if>
    );
    cmp.init();
    <#if !ComponentHelper.isTimesync(comp)>
      cmp.start();
    </#if>
    ${tc.includeArgs("template.deploy.KeepAlive", [comp, config])}
    }
  </#list>
</#if>