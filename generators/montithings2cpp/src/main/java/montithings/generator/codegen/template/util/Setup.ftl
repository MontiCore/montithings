# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.util

import arcbasis._ast.ASTConnector
import arcbasis._ast.ASTPortAccess
import arcbasis._symboltable.ComponentTypeSymbol
import montithings._ast.ASTMTComponentType
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.ConfigParams-->


class Setup {
  
  def static print(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    if (comp.isAtomic) {
      return printSetupAtomic(comp, compname)
    } else {
      return printSetupComposed(comp, compname, config)
    }

  }
  
  def static printSetupAtomic(ComponentTypeSymbol comp, String compname) {
    return '''
    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp, false)}::setUp(TimeMode enclosingComponentTiming){
      if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}
      <#if comp.presentParentComponent>
 super.setUp(enclosingComponentTiming);
 </#if>
      initialize();
    }
    '''
  }
  
  def static printSetupComposed(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    return '''
    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp, false)}::setUp(TimeMode enclosingComponentTiming){
      if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}
      <#if comp.presentParentComponent>
 super.setUp(enclosingComponentTiming);
 </#if>


    <#if config.getSplittingMode() == ConfigParams.SplittingMode.OFF>
      <#list comp.subComponents as subcomponent >
 ${subcomponent.name}.setUp(enclosingComponentTiming);
 </#list>
      
      ${FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()}
        ${FOR ASTPortAccess target : connector.targetList}
        ${IF !ComponentHelper.isIncomingPort(comp, target)}
        // implements "${connector.source.getQName} -> ${target.getQName}"
        ${Utils.printGetPort(target)}->setDataProvidingPort (${Utils.printGetPort(connector.source)});
        </#if>
        </#list>
      </#list>
    </#if>
    }
    '''
  }
}