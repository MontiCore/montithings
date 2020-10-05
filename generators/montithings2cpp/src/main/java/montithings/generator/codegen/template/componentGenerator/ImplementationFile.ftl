<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "useWsPorts")}
<#import "/template/util/Ports.ftl" as Ports>
<#import "/template/util/Setup.ftl" as Setup>
<#import "/template/util/Init.ftl" as Init>
<#import "/template/util/Subcomponents.ftl" as Subcomponents>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

#include "${compname}.h"
#include ${"<regex>"}
${Utils.printNamespaceStart(comp)}
<#if !comp.hasTypeParameter()>
    <@generateBody comp compname config/>
</#if>
${Utils.printNamespaceEnd(comp)}

<#macro generateBody comp compname config >
    <@Ports.printMethodBodies comp.getPorts() comp compname config/>

    <#if comp.isDecomposed()>
        <#if config.getSplittingMode().toString() != "OFF">
            <@Subcomponents.printMethodDefinitions comp config/>
        </#if>

        <#if ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp)>
            <@printRun comp compname/>
        </#if>
        <@printComputeDecomposed comp compname config/>
        <@printStartDecomposed comp compname config/>
    <#else>
        <@printComputeAtomic comp compname/>
        <@printStartAtomic comp compname/>
        <@printRun comp compname/>

        ${Utils.printTemplateArguments(comp)}
        void ${compname}${Utils.printFormalTypeParameters(comp)}::initialize(){
        <#list comp.incomingPorts as port >
          getPort${port.getName()?cap_first} ()->registerListeningPort (this->getUuid ());
        </#list>
        ${compname}Result${Utils.printFormalTypeParameters(comp)} result = behaviorImpl<#-- TODO ${Identifier.getBehaviorImplName()}-->.getInitialValues();
        setResult(result);
        }

        ${Utils.printTemplateArguments(comp)}
        void ${compname}${Utils.printFormalTypeParameters(comp)}::setResult(${compname}Result${Utils.printFormalTypeParameters(comp)} result){
        <#list comp.getOutgoingPorts() as portOut >
          this->getPort${portOut.getName()?cap_first}()->setNextValue(result.get${portOut.getName()?cap_first}());
        </#list>
      }
    </#if>

    <@printShouldComputeCheck comp compname/>

    <@Setup.print comp compname config/>

    <@Init.print comp compname config/>

    <@printConstructor comp compname config/>
</#macro>


<#macro printConstructor comp compname config>
    <#assign shouldPrintSubcomponents = comp.subComponents?has_content && (config.getSplittingMode().toString() == "OFF")>
    ${Utils.printTemplateArguments(comp)}
    ${compname}${Utils.printFormalTypeParameters(comp)}::${compname}(std::string instanceName<#if comp.getParameters()?has_content>
  ,
</#if>${Utils.printConfigurationParametersAsList(comp)})
    <#if comp.isAtomic() || comp.getParameters()?has_content || shouldPrintSubcomponents>
      :
    </#if>
    <#if comp.isAtomic()>
        <@printBehaviorInitializerListEntry comp compname/>
    </#if>
    <#if comp.isAtomic() && comp.getParameters()?has_content>
      ,
    </#if>
    <#if shouldPrintSubcomponents>
        <@Subcomponents.printInitializerList comp config/>
    </#if>
    <#if comp.getParameters()?has_content && shouldPrintSubcomponents>,</#if>
    <#if comp.isAtomic() && !comp.getParameters()?has_content && shouldPrintSubcomponents>,
    </#if>
    <#list comp.getParameters() as param >
        ${param.getName()} (${param.getName()})<#sep>,
    </#list>
  {
  this->instanceName = instanceName;
    <#if comp.isPresentParentComponent()>
      super(<#list getInheritedParams(comp) as inhParam >
      inhParam<#sep>,
    </#list>);
    </#if>
  }
</#macro>

<#macro printBehaviorInitializerListEntry comp compname>
  behaviorImpl<#-- TODO ${Identifier.getBehaviorImplName()}-->(${compname}Impl${Utils.printFormalTypeParameters(comp, false)}(
    <#if comp.hasParameters()>
        <#list comp.getParameters() as param >
          param.getName()<#sep>,
        </#list>
    </#if>
  ))
</#macro>

<#macro printComputeAtomic comp compname>
    ${Utils.printTemplateArguments(comp)}
  void ${compname}${Utils.printFormalTypeParameters(comp)}::compute() {
  if (shouldCompute())
  {
    <@printComputeInputs comp compname/>
    ${compname}Result${Utils.printFormalTypeParameters(comp)} result;
    <#list comp.incomingPorts as port>
    <#--  ${ValueCheck.printPortValuecheck(comp, port)} -->
    </#list>
    <@printPreconditionsCheck comp compname/>
  result = behaviorImpl<#-- TODO ${Identifier.getBehaviorImplName()}-->.compute(input);
    <#list comp.getOutgoingPorts() as port>
    <#--  ${ValueCheck.printPortValuecheck(comp, port)} -->
    </#list>
    <@printPostconditionsCheck comp compname/>
  setResult(result);
  }
  }
</#macro>

<#macro printComputeInputs comp compname isMonitor=false>
    <#if !ComponentHelper.usesBatchMode(comp)>
        ${compname}Input${Utils.printFormalTypeParameters(comp)} input<#if comp.getAllIncomingPorts()?has_content>(<#list comp.getAllIncomingPorts() as inPort >getPort${inPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${inPort.getName()?cap_first}<#else>this->uuid</#if>
      )<#sep>,
    </#list>)</#if>;
    <#else>
        ${compname}Input${Utils.printFormalTypeParameters(comp)} input;
        <#list ComponentHelper.getPortsInBatchStatement(comp) as inPort>
          while(getPort${inPort.getName()?cap_first}()->hasValue(this->uuid)){
          input.add${inPort.getName()?cap_first}Element(getPort${inPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${inPort.getName()?cap_first}

        <#else>
          this->uuid
        </#if>));
          }
        </#list>
        <#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort >
          input.add${inPort.getName()?cap_first}Element(getPort${inPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${inPort.getName()?cap_first}

        <#else>
          this->uuid
        </#if>));
        </#list>
    </#if>
</#macro>

<#macro printShouldComputeCheck comp compname>
    ${Utils.printTemplateArguments(comp)}
  bool ${compname}${Utils.printFormalTypeParameters(comp)}::shouldCompute() {
    <#if comp.getAllIncomingPorts()?size gt 0 && !ComponentHelper.hasSyncGroups(comp)>
      if (timeMode == TIMESYNC || <#list comp.getAllIncomingPorts() as inPort>getPort${inPort.getName()?cap_first}
      ()->hasValue(this->uuid)<#sep>||</#sep>
    </#list>)
      { return true; }
    </#if>
    <#if ComponentHelper.hasSyncGroups(comp)>
      if (
        <#list ComponentHelper.getSyncGroups(comp) as syncGroup >
          (<#list syncGroup as port >
          getPort${port?cap_first}()->hasValue(this->uuid)<#sep>&&</#sep>
        </#list>)
            <#sep>||</#sep>
        </#list>
        <#if ComponentHelper.getPortsNotInSyncGroup(comp)?size gt 0>
          || <#list ComponentHelper.getPortsNotInSyncGroup(comp) as port > getPort${port.getName()?cap_first}
          ()->hasValue(this->uuid)<#sep>||</#sep>
        </#list>
          <</#if>
      )
      { return true; }
    </#if>
    <#if comp.getAllIncomingPorts()?size == 0>
      return true;
    <#else>
      return false;
    </#if>
  }
</#macro>

<#macro printPreconditionsCheck comp compname>
    <#assign preconditions = ComponentHelper.getPreconditions(comp)>
    <#list preconditions as statement>
      if (
        <#list ComponentHelper.getPortsInGuardExpression(statement.guard) as port>
            <#if !ComponentHelper.isBatchPort(port, comp) && !ComponentHelper.portIsComparedToNoData(statement.guard, port.getName())>
              input.get${port.getName()?cap_first}()
            <#else>
              true // presence of value on port ${port.getName()} not checked as it is compared to NoData
            </#if>
            <#sep>&&</#sep>
        </#list>
        <#if ComponentHelper.getPortsInGuardExpression(statement.guard)?size == 0>
          true // presence of value on ports not checked as they are not used in precondition
        </#if>
      &&
      !(
        ${Utils.printExpression(statement.guard)}
      )) {
        <#if ComponentHelper.getCatch(comp, statement)??>
            ${ComponentHelper.printJavaBlock(ComponentHelper.getCatch(comp, statement).get().handler)}
        <#else>
          std::stringstream error;
          error << "Violated precondition ${Utils.printExpression(statement.guard, false)} on component ${comp.packageName}.${compname}" << std::endl;
          error << "Input port values: " << std::endl;
            <#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort>
              if (input.get${inPort.getName()?cap_first} ().has_value()) {
              error << "Port \"${inPort.getName()}\": " << input.get${inPort.getName()?cap_first} ().value() << std::endl;
              } else {
              error << "Port \"${inPort.getName()}\": No data." << std::endl;
              }
            </#list>
            <#list ComponentHelper.getPortsInBatchStatement(comp) as inPort>
              if (input.get${inPort.getName()?cap_first} ().has_value()) {
              error << "Port \"${inPort.getName()}\": " << input.get${inPort.getName()?cap_first} () << std::endl;
              } else {
              error << "Port \"${inPort.getName()}\": No data." << std::endl;
              }
            </#list>
          throw std::runtime_error(error.str ());
        </#if>
      }
    </#list>
</#macro>

<#macro printPostconditionsCheck comp compname>
    <#assign postconditions = ComponentHelper.getPostconditions(comp)>
    <#list postconditions as statement>
      if (
        <#list ComponentHelper.getPortsInGuardExpression(statement.guard) as port>
            <#if !ComponentHelper.isBatchPort(port, comp) && !ComponentHelper.portIsComparedToNoData(statement.guard, port.getName())>
                <#if port.isIncoming>
                  input.get${port.getName()?cap_first}()
                <#else>
                  result.get${port.getName()?cap_first}()
                </#if>
            <#else>
              true // presence of value on port ${port.getName()} not checked as it is compared to NoData
            </#if>
            <#sep>&&</#sep>
        </#list>
        <#if ComponentHelper.getPortsInGuardExpression(statement.guard)?size == 0>
          true // presence of value on ports not checked as they are not used in precondition
        </#if>
      &&
      !(
        ${Utils.printExpression(statement.guard)}
      )) {
        <#if ComponentHelper.getCatch(comp, statement)??>
            ${ComponentHelper.printJavaBlock(ComponentHelper.getCatch(comp, statement).get().handler)}
        <#else>
          std::stringstream error;
          error << "Violated postcondition ${Utils.printExpression(statement.guard, false)} on component ${comp.packageName}.${compname}" << std::endl;
          error << "Port values: " << std::endl;
            <#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort>
              if (input.get${inPort.getName()?cap_first} ().has_value()) {
              error << "In port \"${inPort.getName()}\": " << input.get${inPort.getName()?cap_first} ().value() << std::endl;
              } else {
              error << "In port \"${inPort.getName()}\": No data." << std::endl;
              }
            </#list>
            <#list ComponentHelper.getPortsInBatchStatement(comp) as inPort>
              if (input.get${inPort.getName()?cap_first} ().has_value()) {
              error << "In port \"${inPort.getName()}\": " << input.get${inPort.getName()?cap_first} () << std::endl;
              } else {
              error << "In port \"${inPort.getName()}\": No data." << std::endl;
              }
            </#list>
            <#list comp.getAllOutgoingPorts() as outPort>
              if (result.get${outPort.getName()?cap_first} ().has_value()) {
              error << "Out port \"${outPort.getName()}\": " << result.get${outPort.getName()?cap_first} ().value() << std::endl;
              } else {
              error << "Out port \"${outPort.getName()}\": No data." << std::endl;
              }
            </#list>
          throw std::runtime_error(error.str ());
        </#if>
      }
    </#list>
</#macro>

<#macro printComputeDecomposed comp compname config>
    ${Utils.printTemplateArguments(comp)}
  void ${compname}${Utils.printFormalTypeParameters(comp)}::compute(){
  if (shouldCompute()) {

    <@printComputeInputs comp compname/>
    <#list comp.incomingPorts as port>
    <#-- ${ValueCheck.printPortValuecheck(comp, port)} -->
    </#list>
    <@printPreconditionsCheck comp compname/>

    <#if config.getSplittingMode().toString() == "OFF">
        <#list comp.subComponents as subcomponent >
          this->${subcomponent.getName()}.compute();
        </#list>
    </#if>

    <@printComputeResults comp compname true/>
    <#list comp.getOutgoingPorts() as port>
    <#-- ${ValueCheck.printPortValuecheck(comp, port)} -->
    </#list>
    <@printPostconditionsCheck comp compname/>
  }
  }
</#macro>

<#macro printComputeResults comp compname isMonitor>
    ${compname}Result${Utils.printFormalTypeParameters(comp)} result;
    <#list comp.getAllOutgoingPorts() as outPort>
      if (getPort${outPort.getName()?cap_first}()->hasValue(<#if isMonitor>portMonitorUuid${outPort.getName()?cap_first}

    <#else>
      this->uuid
    </#if>)) {
      result.set${outPort.getName()?cap_first}(getPort${outPort.getName()?cap_first}()->getCurrentValue(<#if isMonitor>portMonitorUuid${outPort.getName()?cap_first}

    <#else>
      this->uuid
    </#if>).value());
      }
    </#list>
</#macro>

<#macro printStartDecomposed comp compname config >
    ${Utils.printTemplateArguments(comp)}
  void ${compname}${Utils.printFormalTypeParameters(comp)}::start(){
    <#if ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp)>
      threads.push_back(std::thread{&${compname}${Utils.printFormalTypeParameters(comp)}::run, this});
    <#else>
        <#if config.getSplittingMode().toString() == "OFF">
            <#list comp.subComponents as subcomponent >
              this->${subcomponent.getName()}.start();
            </#list>
        </#if>
    </#if>
  }
</#macro>

<#macro printStartAtomic comp compname >
    ${Utils.printTemplateArguments(comp)}
  void ${compname}${Utils.printFormalTypeParameters(comp)}::start(){
  threads.push_back(std::thread{&${compname}${Utils.printFormalTypeParameters(comp)}::run, this});
  }
</#macro>

<#macro printRun comp compname >
    ${Utils.printTemplateArguments(comp)}
  void
    ${compname}${Utils.printFormalTypeParameters(comp)}::run ()
  {
  std::cout << "Thread for ${compname} started\n";

  while (true)
  {
  auto end = std::chrono::high_resolution_clock::now()
  + ${ComponentHelper.getExecutionIntervalMethod(comp)};
  this->compute();

  do {
  std::this_thread::yield();
  std::this_thread::sleep_for(std::chrono::milliseconds(1));
  } while (std::chrono::high_resolution_clock::now()  < end);
  }
  }

</#macro>

<#--def protected static List<String> getInheritedParams(ComponentTypeSymbol component) {
  <#assign List<String> result = new ArrayList;>
  <#assign List<FieldSymbol> configParameters = component.getParameters();>
  if (component.isPresentParentComponent()) {
    <#assign ComponentTypeSymbolLoader superCompReference = component.getParent();>
    <#assign List<FieldSymbol> superConfigParams = superCompReference.getLoadedSymbol()>
    .getParameters();
    if (configParameters?has_content()) {
      for (<#assign i = 0; i < superConfigParams.size(); i++) {>
        result.add(configParameters.get(i).getName());
      }
    }
  }
  return result;
}-->