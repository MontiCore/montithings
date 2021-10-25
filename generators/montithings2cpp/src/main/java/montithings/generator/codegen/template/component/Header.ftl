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
<#if config.getMessageBroker().toString() == "MQTT">
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
<#if !(comp.getPorts()?size == 0)>
  ${tc.includeArgs("template.interface.hooks.Member", [comp])}
</#if>

${tc.includeArgs("template.component.declarations.PortMonitorUuid", [comp, config])}
${tc.includeArgs("template.component.declarations.ThreadsAndMutexes", [comp, config])}
${tc.includeArgs("template.component.declarations.Timemode", [comp, config])}
${tc.includeArgs("template.component.declarations.DDS", [config])}

${tc.includeArgs("template.logtracing.hooks.VariableDeclaration", [comp, config])}

${tc.includeArgs("template.prepostconditions.hooks.Member", [comp])}
${tc.includeArgs("template.state.hooks.Member", [comp])}

${compname}State${Utils.printFormalTypeParameters(comp)} ${Identifier.getStateName()}__at__pre;

<#if comp.isDecomposed()>
  <#if ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp, config)>
    void run();
  </#if>
  ${tc.includeArgs("template.component.helper.SubcompIncludes", [comp, config])}
<#else>
  ${compname}Impl${Utils.printFormalTypeParameters(comp)} ${Identifier.getBehaviorImplName()};

  void setResult(${compname}Result${Utils.printFormalTypeParameters(comp)} result);
  void run();
  <#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
    void run${ComponentHelper.getEveryBlockName(comp, everyBlock)}();
  </#list>
</#if>
void initialize();

public:
${className}(std::string instanceName
<#if comp.getParameters()?has_content>,</#if>
${TypesPrinter.printConstructorArguments(comp)});

<#if config.getMessageBroker().toString() == "MQTT">
  void onMessage (mosquitto *mosquitto, void *obj, const struct mosquitto_message *message) override;
  void publishConnectors();
  void publishConfigForSubcomponent (std::string instanceName);
</#if>

<#if config.getMessageBroker().toString() == "DDS">
  // sensor actuator ports require cmd args in order to set up their DDS clients
  void setDDSCmdArgs (int argc, char *argv[]);
</#if>

<#if comp.isDecomposed()>
  <#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "OFF">
    ${tc.includeArgs("template.component.helper.SubcompMethodDeclarations", [comp, config])}
  </#if>
  <#if config.getSplittingMode().toString() == "OFF">
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

<#if !(comp.getPorts()?size == 0)>
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
  bool shouldCompute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}();
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
};

<#if Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.component.Body", [comp, config, className])}
</#if>

${Utils.printNamespaceEnd(comp)}
