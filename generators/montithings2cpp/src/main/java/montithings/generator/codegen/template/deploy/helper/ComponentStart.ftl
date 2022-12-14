<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

${tc.includeArgs("template.deploy.helper.MqttInit", [comp, config])}

<#-- NO TEMPLATE ARGUMENTS -->
<#if config.getTypeArguments(comp)?size == 0>

  ${tc.includeArgs("template.deploy.helper.DDSInit", [comp, config])}
  ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name} cmp (
  instanceNameArg.getValue ()
  <#if brokerIsMQTT>
  , mqttClientInstance
  , mqttClientLocalInstance
  </#if>
  <#if comp.getParameters()?size gt 0>,</#if>
  <#list comp.getParameters() as variable>
      ${variable.getName()} <#sep>,</#sep>
  </#list>
  );

  <#-- Printing connection strings for dynamically instantiated components -->
  <#if comp.getPorts()?size gt 0>
    if (printConnectionStr.getValue ())
    {
    if (ppprintConnectionStr.getValue ())
    {
    ${tc.includeArgs("template.deploy.helper.GetConnectString", [comp, config, TypesHelper.getComponentTypePrefix() + comp.getName(), true])}
    LOG(INFO) << "Co${comp.getName()} Connection String: " << str;
    } else {
      ${tc.includeArgs("template.deploy.helper.GetConnectString", [comp, config, TypesHelper.getComponentTypePrefix() + comp.getName(), false])}
      LOG(INFO) << "Co${comp.getName()} Connection String: " << str;
    }
    }
    if (sendConnectionStr.getValue ())
    {
      cmp.sendConnectionString ("/dynamics", cmp.getConnectionString${TypesHelper.getComponentTypePrefix() + comp.getName()}())
    }
    <#list ComponentHelper.getInterfaceClassNames(comp) as interface>
      if (printConnectionStr${interface}.getValue ())
      {
      if (ppprintConnectionStr.getValue ())
      {
      ${tc.includeArgs("template.deploy.helper.GetConnectString", [comp, config, interface, true])}
      LOG(INFO) << "${interface} Connection String: " << str;
      } else {
      ${tc.includeArgs("template.deploy.helper.GetConnectString", [comp, config, interface, false])}
      LOG(INFO) << "${interface} Connection String: " << str;
      }
      }
      if (sendConnectionStr${interface}.getValue ())
      {
        cmp.sendConnectionString ("/dynamics", cmp.getConnectionString${interface}())
      }
    </#list>
  </#if>

  ${tc.includeArgs("template.deploy.helper.DDSClientSetCmp", [comp, config])}
  ${tc.includeArgs("template.deploy.helper.CommunicationManagerInit", [comp, config])}

  <#list ComponentHelper.getSIUnitPortNames(comp) as portName>
    cmp.getInterface()->setPort${portName?cap_first}ConversionFactor(${portName}ConversionFactor);
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
  ${tc.includeArgs("template.deploy.helper.KeepAlive", [comp, config])}

<#else>
  <#-- WITH TEMPLATE ARGUMENTS -->
  <#list config.getTypeArguments(comp) as typeArguments>
    ${tc.includeArgs("template.deploy.helper.DDSInit", [comp, config])}
    <#if typeArguments?counter gt 1>else</#if>
    if (_typeArgs == "${typeArguments}")
    {
      ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name}${"<"}${typeArguments}${">"} cmp (
      instanceNameArg.getValue ()
      <#if brokerIsMQTT>
        , mqttClientInstance
        , mqttClientLocalInstance
      </#if>
      <#if comp.getParameters()?size gt 0>,</#if>
      <#list comp.getParameters() as variable>
        ${variable.getName()} <#sep>,</#sep>
      </#list>
    );
    ${tc.includeArgs("template.deploy.helper.DDSClientSetCmp", [comp, config])}
    ${tc.includeArgs("template.deploy.helper.CommunicationManagerInit", [comp, config])}

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
    ${tc.includeArgs("template.deploy.helper.KeepAlive", [comp, config])}
    }
  </#list>
</#if>