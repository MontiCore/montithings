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
${tc.includeArgs("template.interface.hooks.Member", [comp])}

${tc.includeArgs("template.component.declarations.PortMonitorUuid", [comp, config])}
${tc.includeArgs("template.component.declarations.ThreadsAndMutexes", [comp, config])}
${tc.includeArgs("template.component.declarations.Timemode", [comp, config])}
${tc.includeArgs("template.component.declarations.DDS", [config])}


<#if ComponentHelper.isDSLComponent(comp,config)>
  std::string brokerHostName;
  int brokerPort;
</#if>
<#if brokerIsMQTT>
  MqttClient *  mqttClientInstance;
  MqttClient *  mqttClientLocalInstance;
  <#if ComponentHelper.shouldGenerateCompatibilityHeartbeat(comp, config)>
    MqttClient *  mqttClientCompatibilityInstance;
    <#if ComponentHelper.getPortsWithTestBlocks(comp)?size <= 0>
      bool isConnectedToOtherComponent = false;
      MqttClient *  mqttClientSenderInstance;
      bool mqttClientSenderInstanceHasBeenConnected = false;
      std::set< std::string> subscriptionsToSend;
    <#else>
      <#list ComponentHelper.getPortsWithTestBlocks(comp) as p>
        bool isConnected${p.getName()} = false;
        MqttClient *  mqttClientSenderInstance${p.getName()};
        bool mqttClientSenderInstance${p.getName()}HasBeenConnected = false;
        std::set< std::string> subscriptionsToSend${p.getName()};
      </#list>
    </#if>
    std::string ip_address = "";
  </#if>
  json sensorActuatorTypes;

  <#if needsProtobuf>
      ${compname}Input${generics} input__cache;
      ${compname}Result${generics} result__cache;
  </#if>

  <#list comp.getOutgoingPorts() + comp.getIncomingPorts() as p>
    <#assign type = TypesPrinter.getRealPortCppTypeString(comp, p, config)>
    MqttPort<Message<${type}>> *${p.getName()};
    <#if needsProtobuf && hasNonCppHwc>
      MqttPort<Message<${type}>> *${p.getName()}_protobuf;
    </#if>
    <#if GeneratorHelper.getMqttSensorActuatorName(p, config).isPresent()>
      std::thread th${p.getName()?cap_first};
      std::promise<void> exitSignal${p.getName()?cap_first};
      std::string currentTopic${p.getName()?cap_first};
    </#if>
  </#list>

  <#if ComponentHelper.shouldGenerateCompatibilityHeartbeat(comp, config)>
    std::thread th__Compatibility;
  </#if>
  std::promise<void> exitSignal__Compatibility;
</#if>

${tc.includeArgs("template.logtracing.hooks.VariableDeclaration", [comp, config])}

${tc.includeArgs("template.prepostconditions.hooks.Member", [comp])}
${tc.includeArgs("template.state.hooks.Member", [comp])}

${compname}State${Utils.printFormalTypeParameters(comp)} ${Identifier.getStateName()}__at__pre;
<#if comp.isAtomic() || ComponentHelper.getPortSpecificBehaviors(comp)?size gt 0>
  ${compname}Impl${Utils.printFormalTypeParameters(comp)} ${Identifier.getBehaviorImplName()};
</#if>

<#if comp.isDecomposed()>
  <#if needsRunMethod>
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

<#if ComponentHelper.isDSLComponent(comp,config)>
void python_receiver(std::string payload);
void python_start();
int lastPyPID = -1;
</#if>


// if set to true, loop-threads will stop after their current iteration
bool stopSignalReceived = false;

public:
${className}(std::string instanceName
<#if brokerIsMQTT>
  , MqttClient* passedMqttClientInstance
  , MqttClient* passedMqttClientLocalInstance
</#if>
<#if ComponentHelper.isDSLComponent(comp,config)>
  , std::string brokerHostNameArg
  , int brokerPortArg
</#if>
<#if comp.getParameters()?has_content>,</#if>
${TypesPrinter.printConstructorArguments(comp)});

<#if brokerIsMQTT>
  void onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) override;
  void publishConnectors();
  void publishConfigForSubcomponent (std::string instanceName);
  void sendKeepAlive(std::string sensorActuatorConfigTopic, std::string portName, std::string typeName, std::future<void> keepAliveFuture);
  void sendConnectionString (std::string connectionStringTopic, std::string connectionString);
  MqttClient *getMqttClientInstance () const;
  <#if ComponentHelper.shouldGenerateCompatibilityHeartbeat(comp, config)>
    <#if ComponentHelper.getPortsWithTestBlocks(comp)?size <= 0>
      MqttClient *getMqttClientSenderInstance() const;
      std::set< std::string> *getSubscriptionsToSend();
    <#else>
      <#list ComponentHelper.getPortsWithTestBlocks(comp) as p>
        MqttClient *getMqttClientSenderInstance${p.getName()}() const;
        std::set< std::string> *getSubscriptionsToSend${p.getName()}();
        void setIsConnected${p.getName()}();
      </#list>
    </#if>
    void sendCompatibilityHeartbeat(std::future<void> keepAliveFuture);
  </#if>
</#if>

<#if brokerIsDDS>
  // sensor actuator ports require cmd args in order to set up their DDS clients
  void setDDSCmdArgs (int argc, char *argv[]);
</#if>



<#if comp.isDecomposed()>
  <#if !(splittingModeDisabled) && brokerDisabled>
    ${tc.includeArgs("template.component.helper.SubcompMethodDeclarations", [comp, config])}
  </#if>
  <#if splittingModeDisabled>
    <#list comp.getSubComponents() as subcomponent>
      <#if Utils.getGenericParameters(comp)?seq_contains(subcomponent.getGenericType().getName())>
        <#assign type = subcomponent.getGenericType().getName()>
      <#else>
        <#assign type = ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, config)>
      </#if>
      ${Utils.printPackageNamespace(comp, subcomponent)}${type}*
      getSubcomp__${subcomponent.getName()?cap_first} ();
    </#list>
  </#if>
</#if>

<#if ComponentHelper.componentHasPorts(comp)>
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
void stop();
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
