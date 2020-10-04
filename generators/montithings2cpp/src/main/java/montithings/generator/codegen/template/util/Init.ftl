<#-- (c) https://github.com/MontiCore/monticore -->
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
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
      <@printInitAtomic comp compname/>
     <else>
      <@printInitComposed comp compname config/>
    </#if>
</#macro>

<#macro printInitAtomic comp compname>
    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp, false)}::init(){
      <#if comp.isPresentParentComponent()>
 super.init();
 </#if>
    }    
</#macro>

<#macro printInitComposed comp compname config>
    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp, false)}::init(){
    <#if comp.isPresentParentComponent()>
 super.init();
 </#if>

    <#if config.getSplittingMode().toString() == "OFF">
    <#list comp.getAstNode().getConnectors() as connector>
      <#list connector.getTargetList() as target>
      <#if ComponentHelper.isIncomingPort(comp, target)>
        // implements "${connector.getSource()..getQName()} -> ${target..getQName()}"
        ${Utils.printGetPort(target)}->setDataProvidingPort (${Utils.printGetPort(connector.getSource())});
      </#if>
      </#list>
    </#list>

    <#list comp.getSubComponents() as subcomponent >
 ${subcomponent.getName()}.init();
 </#list>
    </#if>
    }
</#macro>