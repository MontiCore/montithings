# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.util

import arcbasis._ast.ASTConnector
import arcbasis._ast.ASTPortAccess
import arcbasis._symboltable.ComponentTypeSymbol
import montithings._ast.ASTMTComponentType
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.ConfigParams-->


class Init {
  def static print(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    if (comp.isAtomic) {
      return printInitAtomic(comp, compname)
    } else {
      return printInitComposed(comp, compname, config)
    }
  }

  def static printInitAtomic(ComponentTypeSymbol comp, String compname) {
    return '''
    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp, false)}::init(){
      <#if comp.presentParentComponent>
 super.init();
 </#if>
     
       
    }    
    '''
  }
  
  def static printInitComposed(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    return '''
    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp, false)}::init(){
    <#if comp.presentParentComponent>
 super.init();
 </#if>

    <#if config.getSplittingMode() == ConfigParams.SplittingMode.OFF>
    ${FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()}
      ${FOR ASTPortAccess target : connector.targetList}
      ${IF ComponentHelper.isIncomingPort(comp, target)}
        // implements "${connector.source.getQName} -> ${target.getQName}"
        ${Utils.printGetPort(target)}->setDataProvidingPort (${Utils.printGetPort(connector.source)});
      </#if>
      </#list>
    </#list>

    <#list comp.subComponents as subcomponent >
 ${subcomponent.name}.init();
 </#list>
    </#if>
    }
    '''
  }
  
}