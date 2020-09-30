# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import arcbasis._symboltable.ComponentTypeSymbolLoader
import de.monticore.types.typesymbols._symboltable.FieldSymbol
import java.util.ArrayList
import java.util.List
import montithings.generator.codegen.ConfigParams
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.codegen.xtend.util.Init
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.codegen.xtend.util.Setup
import montithings.generator.codegen.xtend.util.Subcomponents
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper-->

class ComponentGenerator {
  
  def static generateHeader(ComponentTypeSymbol comp, String compname, ConfigParams config, boolean useWsPorts) {
    <#assign ComponentHelper helper = new ComponentHelper(comp)>
      
    return '''
    #pragma once
    #include "IComponent.h"
    #include "Port.h"
    #include "InOutPort.h"
    #include <string>
    #include <map>
    #include <vector>
    #include <list>
    #include <set>
    #include <thread>
    #include "sole/sole.hpp"
    #include <iostream>
    ${Ports.printIncludes(comp, config)}
    
    <#if comp.isDecomposed>
    ${Subcomponents.printIncludes(comp, compname, config)}
    ${ELSE}
    #include "${compname}Impl.h"
    #include "${compname}Input.h"
    #include "${compname}Result.h"
    </#if>
    
    ${Utils.printNamespaceStart(comp)}

    ${Utils.printTemplateArguments(comp)}
    class ${compname} : public IComponent <#if comp.presentParentComponent> , ${Utils.printSuperClassFQ(comp)}
                <#if comp.parent.loadedSymbol.hasTypeParameter><<#list helper.superCompActualTypeArguments as scTypeParams >
 scTypeParams<#sep>,
 </#list>>
                </#if></#if>
    {
    private:
      ${Ports.printVars(comp, comp.ports, config)}
      ${Utils.printVariables(comp)}
      
<#-- Currently useless. MontiArc 6's getFields() returns both variables and parameters --><#-- Utils.printConfigParameters(comp) -->
      std::vector< std::thread > threads;
      TimeMode timeMode = <#if ComponentHelper.isTimesync(comp)>
 TIMESYNC
 <#else>
 EVENTBASED
  </#if>;
      <#if comp.isDecomposed>
      <#if ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp)>
 void run();
 </#if>
      ${Subcomponents.printVars(comp, config)}
      ${ELSE}

      ${compname}Impl${Utils.printFormalTypeParameters(comp)} ${Identifier.behaviorImplName};

      void initialize();
      void setResult(${compname}Result${Utils.printFormalTypeParameters(comp)} result);
      void run();
      </#if>
      
    public:
      ${Ports.printMethodHeaders(comp.ports, config)}
      ${compname}(std::string instanceName<#if !comp.parameters.isEmpty>
 ,
 </#if>${ComponentHelper.printConstructorArguments(comp)});

      <#if comp.isDecomposed>
      <#if config.getSplittingMode() != ConfigParams.SplittingMode.OFF>
 ${Subcomponents.printMethodDeclarations(comp, config)}
 </#if>
      </#if>
      
      void setUp(TimeMode enclosingComponentTiming) override;
      void init() override;
      void compute() override;
      bool shouldCompute();
      void start() override;
    };
                
    <#if comp.hasTypeParameter()>
 ${generateBody(comp, compname, config)}
 </#if>

      ${Utils.printNamespaceEnd(comp)}
    '''
  }

  def static generateImplementationFile(ComponentTypeSymbol comp, String compname, ConfigParams config, boolean useWsPorts) {
    return '''
    #include "${compname}.h"
    #include <regex>
    ${Utils.printNamespaceStart(comp)}
    <#if !comp.hasTypeParameter()>
 ${generateBody(comp, compname, config)}
 </#if>
    ${Utils.printNamespaceEnd(comp)}
    '''
  }
  
  def static generateBody(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    return '''
    ${Ports.printMethodBodies(comp.ports, comp, compname, config)}
        
    <#if comp.isDecomposed>
    <#if config.getSplittingMode() != ConfigParams.SplittingMode.OFF>
 ${Subcomponents.printMethodDefinitions(comp, config)}
 </#if>

    <#if ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp)>
 ${printRun(comp, compname)}
 </#if>
    ${printComputeDecomposed(comp, compname, config)}
    ${printStartDecomposed(comp, compname, config)}
    ${ELSE}
    ${printComputeAtomic(comp, compname)}
    ${printStartAtomic(comp, compname)}
    ${printRun(comp, compname)}

    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp)}::initialize(){
      <#list comp.incomingPorts as port >
 getPort${port.name.toFirstUpper} ()->registerListeningPort (this->getUuid ());
 </#list>
      ${compname}Result${Utils.printFormalTypeParameters(comp)} result = ${Identifier.behaviorImplName}.getInitialValues();
      setResult(result);
    }

    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp)}::setResult(${compname}Result${Utils.printFormalTypeParameters(comp)} result){
      <#list comp.outgoingPorts as portOut >
 this->getPort${portOut.name.toFirstUpper}()->setNextValue(result.get${portOut.name.toFirstUpper}());
 </#list>
    }
    </#if>
    
    ${printShouldComputeCheck(comp, compname)}

    ${Setup.print(comp, compname, config)}

    ${Init.print(comp, compname, config)}
    
    ${printConstructor(comp, compname, config)}
    '''
  }
  

  def static printConstructor(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    <#assign shouldPrintSubcomponents = !comp.subComponents.isEmpty && (config.getSplittingMode() == ConfigParams.SplittingMode.OFF)>
    return '''
    ${Utils.printTemplateArguments(comp)}
    ${compname}${Utils.printFormalTypeParameters(comp)}::${compname}(std::string instanceName<#if !comp.parameters.isEmpty>
 ,
 </#if>${Utils.printConfigurationParametersAsList(comp)})
    <#if comp.isAtomic || !comp.parameters.isEmpty || shouldPrintSubcomponents>
 :
 </#if>
      <#if comp.isAtomic>
 ${printBehaviorInitializerListEntry(comp, compname)}
 </#if>
      <#if comp.isAtomic && !comp.parameters.isEmpty>
 ,
 </#if>
    <#if shouldPrintSubcomponents>
 ${Subcomponents.printInitializerList(comp, config)}
 </#if>
    <#if !comp.parameters.isEmpty && shouldPrintSubcomponents},${ENDIF>
 <#if comp.isAtomic && comp.parameters.isEmpty && shouldPrintSubcomponents>,
 </#if>
    <#list comp.parameters as param >
 ${param.name} (${param.name})<#sep>,
 </#list>
    {
      this->instanceName = instanceName;
      <#if comp.presentParentComponent>
      super(<#list getInheritedParams(comp) as inhParam >
 inhParam<#sep>,
 </#list>);
      </#if>
    }
    '''
  }
  
  def static printBehaviorInitializerListEntry(ComponentTypeSymbol comp, String compname) {
    return '''
    ${Identifier.behaviorImplName}(${compname}Impl${Utils.printFormalTypeParameters(comp, false)}(
    <#if comp.hasParameters>
          <#list comp.parameters as param >
 param.name<#sep>,
 </#list>
    </#if>
  ))'''.toString().replace("\n", "")
  }
  
  def static printComputeAtomic(ComponentTypeSymbol comp, String compname) {
    return '''
    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp)}::compute() {
      if (shouldCompute())
      {
        ${printComputeInputs(comp, compname)}
        ${compname}Result${Utils.printFormalTypeParameters(comp)} result;
        <#list comp.incomingPorts as port>
 <#--  ${ValueCheck.printPortValuecheck(comp, port)} -->
 </#list>
        ${printPreconditionsCheck(comp, compname)}
        result = ${Identifier.behaviorImplName}.compute(input);
        <#list comp.outgoingPorts as port>
 <#--  ${ValueCheck.printPortValuecheck(comp, port)} -->
 </#list>
        ${printPostconditionsCheck(comp, compname)}
        setResult(result);				
      }
    }
    '''
  }

  def static printComputeInputs(ComponentTypeSymbol comp, String compname) {
    return printComputeInputs(comp, compname, false);
  }
  
  def static printComputeInputs(ComponentTypeSymbol comp, String compname, boolean isMonitor) {
    return '''
    <#if !ComponentHelper.usesBatchMode(comp)>
    ${compname}Input${Utils.printFormalTypeParameters(comp)} input<#if !comp.allIncomingPorts.empty>(<#list comp.allIncomingPorts}getPort${inPort.name.toFirstUpper}()->getCurrentValue(<#if isMonitor>portMonitorUuid${inPort.name.toFirstUpper}${ELSE}this->uuid${ENDIF as inPort >
 )<#sep>,
 </#list>)</#if>;
    ${ELSE}
    ${compname}Input${Utils.printFormalTypeParameters(comp)} input;
    <#list ComponentHelper.getPortsInBatchStatement(comp) as inPort>
    while(getPort${inPort.name.toFirstUpper}()->hasValue(this->uuid)){
      input.add${inPort.name.toFirstUpper}Element(getPort${inPort.name.toFirstUpper}()->getCurrentValue(<#if isMonitor}portMonitorUuid${inPort.name.toFirstUpper>

 <#else>
 this->uuid
  </#if>));
    }
    </#list>
    <#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort >
 input.add${inPort.name.toFirstUpper}Element(getPort${inPort.name.toFirstUpper}()->getCurrentValue(<#if isMonitor}portMonitorUuid${inPort.name.toFirstUpper>

 <#else>
 this->uuid
  </#if>));
 </#list>
    </#if>
    '''
  }
  
  def static printShouldComputeCheck(ComponentTypeSymbol comp, String compname) {
    return '''
    ${Utils.printTemplateArguments(comp)}
    bool ${compname}${Utils.printFormalTypeParameters(comp)}::shouldCompute() {
      ${IF comp.allIncomingPorts.length > 0 && !ComponentHelper.hasSyncGroups(comp)}
      if (timeMode == TIMESYNC || <#list comp.allIncomingPorts}getPort${inPort.name.toFirstUpper as inPort >
 ()->hasValue(this->uuid)<#sep>||</#sep>
 </#list>)
      { return true; }
      </#if>
      <#if ComponentHelper.hasSyncGroups(comp)>
      if ( 
        <#list ComponentHelper.getSyncGroups(comp) as syncGroup >
 (<#list syncGroup as port >
 getPort${port.toFirstUpper}()->hasValue(this->uuid)<#sep>&&</#sep>
 </#list>)
 </#list>
        ${IF ComponentHelper.getPortsNotInSyncGroup(comp).length() > 0}
        || <#list ComponentHelper.getPortsNotInSyncGroup(comp)} getPort${port.name.toFirstUpper as port >
 ()->hasValue(this->uuid)<#sep>||</#sep>
 </#list>
        <</#if>
      )
      { return true; }
      </#if>
      <#if comp.allIncomingPorts.length == 0>
 return true;
 <#else>
 return false;
  </#if>
    }
    '''
  }
  
  def static printPreconditionsCheck(ComponentTypeSymbol comp, String compname) {
    <#assign preconditions = ComponentHelper.getPreconditions(comp);>
    return '''
    <#list preconditions as statement>
    if (
    ${FOR port : ComponentHelper.getPortsInGuardExpression(statement.guard) SEPARATOR ' && '}
      <#if !ComponentHelper.isBatchPort(port, comp) && !ComponentHelper.portIsComparedToNoData(statement.guard, port.name)>
 input.get${port.name.toFirstUpper}()
 <#else>
 true // presence of value on port ${port.name} not checked as it is compared to NoData
  </#if>
    </#list>
    <#if ComponentHelper.getPortsInGuardExpression(statement.guard).isEmpty>
 true // presence of value on ports not checked as they are not used in precondition
 </#if>
     && 
    !(
      ${Utils.printExpression(statement.guard)}
    )) {
      ${IF ComponentHelper.getCatch(comp, statement).isPresent}
        ${ComponentHelper.printJavaBlock(ComponentHelper.getCatch(comp, statement).get().handler)}
      ${ELSE}
      std::stringstream error;
      error << "Violated precondition ${Utils.printExpression(statement.guard, false)} on component ${comp.packageName}.${compname}" << std::endl;
      error << "Input port values: " << std::endl;
      <#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort>
      if (input.get${inPort.name.toFirstUpper} ().has_value()) {
        error << "Port \"${inPort.name}\": " << input.get${inPort.name.toFirstUpper} ().value() << std::endl;
      } else {
        error << "Port \"${inPort.name}\": No data." << std::endl;
      }
      </#list>
      <#list ComponentHelper.getPortsInBatchStatement(comp) as inPort>
      if (input.get${inPort.name.toFirstUpper} ().has_value()) {
        error << "Port \"${inPort.name}\": " << input.get${inPort.name.toFirstUpper} () << std::endl;
      } else {
        error << "Port \"${inPort.name}\": No data." << std::endl;
      }
      </#list>
      throw std::runtime_error(error.str ());
      </#if>
    }
    </#list>
    '''
  }
  
  def static printPostconditionsCheck(ComponentTypeSymbol comp, String compname) {
    <#assign postconditions = ComponentHelper.getPostconditions(comp);>
    return '''
    <#list postconditions as statement>
    if (
    ${FOR port : ComponentHelper.getPortsInGuardExpression(statement.guard) SEPARATOR ' && '}
      ${IF !ComponentHelper.isBatchPort(port, comp) && !ComponentHelper.portIsComparedToNoData(statement.guard, port.name)}
        <#if port.isIncoming>
 input.get${port.name.toFirstUpper}()
 <#else>
 result.get${port.name.toFirstUpper}()
  </#if>
      ${ELSE}
        true // presence of value on port ${port.name} not checked as it is compared to NoData
      </#if>
    </#list>
    <#if ComponentHelper.getPortsInGuardExpression(statement.guard).isEmpty>
 true // presence of value on ports not checked as they are not used in precondition
 </#if>
    && 
    !(
      ${Utils.printExpression(statement.guard)}
    )) {
      ${IF ComponentHelper.getCatch(comp, statement).isPresent}
        ${ComponentHelper.printJavaBlock(ComponentHelper.getCatch(comp, statement).get().handler)}
      ${ELSE}
      std::stringstream error;
      error << "Violated postcondition ${Utils.printExpression(statement.guard, false)} on component ${comp.packageName}.${compname}" << std::endl;
      error << "Port values: " << std::endl;
      <#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort>
      if (input.get${inPort.name.toFirstUpper} ().has_value()) {
        error << "In port \"${inPort.name}\": " << input.get${inPort.name.toFirstUpper} ().value() << std::endl;
      } else {
        error << "In port \"${inPort.name}\": No data." << std::endl;
      }
      </#list>
      <#list ComponentHelper.getPortsInBatchStatement(comp) as inPort>
      if (input.get${inPort.name.toFirstUpper} ().has_value()) {
        error << "In port \"${inPort.name}\": " << input.get${inPort.name.toFirstUpper} () << std::endl;
      } else {
        error << "In port \"${inPort.name}\": No data." << std::endl;
      }
      </#list>
      <#list comp.allOutgoingPorts as outPort>
      if (result.get${outPort.name.toFirstUpper} ().has_value()) {
        error << "Out port \"${outPort.name}\": " << result.get${outPort.name.toFirstUpper} ().value() << std::endl;
      } else {
        error << "Out port \"${outPort.name}\": No data." << std::endl;
      }
      </#list>
      throw std::runtime_error(error.str ());
      </#if>
    }
    </#list>
    '''
  }
  
  def static printComputeDecomposed(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    return '''
    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp)}::compute(){
      if (shouldCompute()) {
      
      ${printComputeInputs(comp, compname)}
      <#list comp.incomingPorts as port>
 <#-- ${ValueCheck.printPortValuecheck(comp, port)} -->
 </#list>
      ${printPreconditionsCheck(comp, compname)}
      
      <#if config.getSplittingMode() == ConfigParams.SplittingMode.OFF>
      <#list comp.subComponents as subcomponent >
 this->${subcomponent.name}.compute();
 </#list>
      </#if>

    ${printComputeResults(comp, compname, true)}
    <#list comp.outgoingPorts as port>
 <#-- ${ValueCheck.printPortValuecheck(comp, port)} -->
 </#list>
    ${printPostconditionsCheck(comp, compname)}
      }
    }
    '''
  }
  
  def static printComputeResults(ComponentTypeSymbol comp, String compname, boolean isMonitor) {
    return '''
    ${compname}Result${Utils.printFormalTypeParameters(comp)} result;
    <#list comp.allOutgoingPorts as outPort>
    if (getPort${outPort.name.toFirstUpper}()->hasValue(<#if isMonitor}portMonitorUuid${outPort.name.toFirstUpper>

 <#else>
 this->uuid
  </#if>)) {
      result.set${outPort.name.toFirstUpper}(getPort${outPort.name.toFirstUpper}()->getCurrentValue(<#if isMonitor}portMonitorUuid${outPort.name.toFirstUpper>

 <#else>
 this->uuid
  </#if>).value());
    }
    </#list>
    '''
  }
  
  def static printStartDecomposed(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    return '''
    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp)}::start(){
      <#if ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp)>
      threads.push_back(std::thread{&${compname}${Utils.printFormalTypeParameters(comp)}::run, this});
      ${ELSE}
      <#if config.getSplittingMode() == ConfigParams.SplittingMode.OFF>
      <#list comp.subComponents as subcomponent >
 this->${subcomponent.name}.start();
 </#list>
      </#if>
      </#if>
    }
    '''
  }
  
  def static printStartAtomic(ComponentTypeSymbol comp, String compname) {
    return '''
    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp)}::start(){
      threads.push_back(std::thread{&${compname}${Utils.printFormalTypeParameters(comp)}::run, this});
    }
    '''
  }
  
  def static printRun(ComponentTypeSymbol comp, String compname) {
    return '''
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

    '''
  }
  
  def protected static List<String> getInheritedParams(ComponentTypeSymbol component) {
    <#assign List<String> result = new ArrayList;>
    <#assign List<FieldSymbol> configParameters = component.getParameters();>
    if (component.isPresentParentComponent()) {
      <#assign ComponentTypeSymbolLoader superCompReference = component.getParent();>
      <#assign List<FieldSymbol> superConfigParams = superCompReference.getLoadedSymbol()>
      .getParameters();
      if (!configParameters.isEmpty()) {
        for (<#assign i = 0; i < superConfigParams.size(); i++) {>
          result.add(configParameters.get(i).getName());
        }
      }
    }
    return result;
  }
}