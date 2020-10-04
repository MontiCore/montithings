<#-- (c) https://github.com/MontiCore/monticore -->
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign ConfigParams = tc.instantiate("montithings.generator.codegen.ConfigParams")>
<#--package montithings.generator.codegen.xtend.util

import arcbasis._ast.ASTConnector
import arcbasis._ast.ASTPortAccess
import arcbasis._symboltable.ComponentTypeSymbol
import montithings._ast.ASTMTComponentType
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.ConfigParams-->


<#macro print comp compname config>
<#if (comp.isAtomic()) >
<@printSetupAtomic comp compname/>
<else>
    <@printSetupComposed comp compname config/>
    </#if>
</#macro>

<#macro printSetupAtomic comp compname>
    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp, false)}::setUp(TimeMode enclosingComponentTiming){
      if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}
      <#if comp.isPresentParentComponent()>
 super.setUp(enclosingComponentTiming);
 </#if>
      initialize();
    }
</#macro>

<#macro printSetupComposed comp compname config>
    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp, false)}::setUp(TimeMode enclosingComponentTiming){
      if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}
      <#if comp.isPresentParentComponent()>
 super.setUp(enclosingComponentTiming);
 </#if>


    <#if config.getSplittingMode().toString() == "OFF">
      <#list comp.getSubComponents() as subcomponent >
 ${subcomponent.getName()}.setUp(enclosingComponentTiming);
 </#list>
      
      <#list comp.getAstNode().getConnectors() as connector>
      <#list connector.getTargetList as target>
        <#if !ComponentHelper.isIncomingPort(comp, target)>
        // implements "${connector.getSource()..getQName()} -> ${target..getQName()}"
        ${Utils.printGetPort(target)}->setDataProvidingPort (${Utils.printGetPort(connector.getSource())});
        </#if>
        </#list>
      </#list>
    </#if>
    }
</#macro>