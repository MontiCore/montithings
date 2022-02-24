<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "useWsPorts", "existsHWC")}
<#include "/template/component/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

${Identifier.createInstance(comp)}

#pragma once
${tc.includeArgs("template.component.helper.Includes", [comp, config, useWsPorts, existsHWC])}

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${className} : public IComponent
<#if brokerIsMQTT>
  , public MqttUser
</#if>
<#if comp.isPresentParentComponent()>
  , ${Utils.printSuperClassFQ(comp)}
<#-- TODO Check if comp.parent().loadedSymbol.hasTypeParameter is operational -->
  <#if comp.parent().loadedSymbol.hasTypeParameter><<#list helper.superCompActualTypeArguments as scTypeParams >
    scTypeParams<#sep>,</#sep>
  </#list>>
  </#if>
</#if>
{
protected:
<#if !hasNoPorts>
  ${tc.includeArgs("template.interface.hooks.Member", [comp])}
</#if>

${tc.includeArgs("template.component.declarations.PortMonitorUuid", [comp, config])}
${tc.includeArgs("template.component.declarations.ThreadsAndMutexes", [comp, config])}
${tc.includeArgs("template.component.declarations.Timemode", [comp, config])}
${tc.includeArgs("template.component.declarations.DDS", [config])}

<#if brokerIsMQTT>
  MqttClient *  mqttClientInstance;
  MqttClient *  mqttClientLocalInstance;
  json sensorActuatorTypes;

  <#list comp.getOutgoingPorts() + comp.getIncomingPorts() as p>
    <#assign type = TypesPrinter.getRealPortCppTypeString(comp, p, config)>
    MqttPort<Message<${type}>> *${p.getName()};
    <#if dummyName5>
      std::thread th${p.getName()?cap_first};
      std::promise<void> exitSignal${p.getName()?cap_first};
    </#if>
  </#list>
</#if>

${tc.includeArgs("template.logtracing.hooks.VariableDeclaration", [comp, config])}

${tc.includeArgs("template.prepostconditions.hooks.Member", [comp])}
${tc.includeArgs("template.state.hooks.Member", [comp])}

${compname}State${Utils.printFormalTypeParameters(comp)} ${Identifier.getStateName()}__at__pre;
<#if dummyName3>
  ${compname}Impl${Utils.printFormalTypeParameters(comp)} ${Identifier.getBehaviorImplName()};
</#if>

<#if comp.isDecomposed()>
  <#if dummyName2>
    void run();
  </#if>
  ${tc.includeArgs("template.component.helper.SubcompIncludes", [comp, config])}
<#else>
  void setResult(${compname}Result${Utils.printFormalTypeParameters(comp)} result);
  void run();
  <#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
    void run${ComponentHelper.getEveryBlockName(comp, everyBlock)}();
  </#list>
</#if>

public:
${className}(std::string instanceName
<#if brokerIsMQTT>
  , MqttClient* passedMqttClientInstance
  , MqttClient* passedMqttClientLocalInstance
</#if>
<#if comp.getParameters()?has_content>,</#if>
${TypesPrinter.printConstructorArguments(comp)});

<#if brokerIsMQTT>
  void onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) override;
  void publishConnectors();
  void publishConfigForSubcomponent (std::string instanceName);
  void sendKeepAlive(std::string sensorActuatorConfigTopic, std::string portName, std::future<void> keepAliveFuture);
  MqttClient *getMqttClientInstance () const;
</#if>

<#if brokerIsDDS>
  // sensor actuator ports require cmd args in order to set up their DDS clients
  void setDDSCmdArgs (int argc, char *argv[]);
</#if>

<#if comp.isDecomposed()>
  <#if !splittingModeDisabled && brokerDisabled>
    ${tc.includeArgs("template.component.helper.SubcompMethodDeclarations", [comp, config])}
  </#if>
  <#if splittingModeDisabled>
    <#list comp.getSubComponents() as subcomponent>
      <#if dummyName4>
        <#assign type = subcomponent.getGenericType().getName()>
      <#else>
        <#assign type = ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, config)>
      </#if>
      ${Utils.printPackageNamespace(comp, subcomponent)}${type}*
      getSubcomp__${subcomponent.getName()?cap_first} ();
    </#list>
  </#if>
</#if>

<#if !hasNoPorts>
  ${tc.includeArgs("template.interface.hooks.MethodDeclaration", [comp])}
</#if>

${tc.includeArgs("template.logtracing.hooks.GetterDeclaration", [comp, config])}
${tc.includeArgs("template.logtracing.hooks.InitLogTracerDeclaration", [comp, config])}

void setUp(TimeMode enclosingComponentTiming) override;
void init() override;
void compute() override;
<#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
  <#assign everyBlockName = ComponentHelper.getEveryBlockName(comp, everyBlock)>
  void compute${everyBlockName} ();
</#list>
bool shouldCompute();
<#list ComponentHelper.getPortSpecificMTBehaviors(comp) as behavior>
  bool shouldCompute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}(<#if !comp.isAtomic()>${compname}Input${generics}& ${Identifier.getInputName()}</#if>);
</#list>
<#list ComponentHelper.getPortSpecificInitBehaviors(comp) as initBehavior>
  bool initialized${ComponentHelper.getPortSpecificInitBehaviorName(comp, initBehavior)} = false;
</#list>
void start() override;
void onEvent () override;
<#if ComponentHelper.retainState(comp)>
  bool restoreState ();
</#if>
<#if !comp.isDecomposed()>
  ${compname}Impl${generics}* getImpl();
</#if>
${compname}State${generics}* getState();
void threadJoin ();
void checkPostconditions(${compname}Input${generics}& input, ${compname}Result${generics}& result, ${compname}State${generics}& state, ${compname}State${generics}& state__at__pre);

<#if comp.getPorts()?size gt 0>
  std::string getConnectionStringCo${compname}() const;
  <#list ComponentHelper.getInterfaceClassNames(comp) as interface>
    std::string getConnectionString${interface}() const;
  </#list>
</#if>
};

<#if Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.component.Body", [comp, config, className])}
</#if>

${Utils.printNamespaceEnd(comp)}
