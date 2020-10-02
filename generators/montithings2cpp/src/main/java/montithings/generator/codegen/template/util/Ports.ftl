# (c) https://github.com/MontiCore/monticore
<#import "/template/adapter/Header.ftl" as Adapter>
<#--package montithings.generator.codegen.xtend.util

import java.util.Collection
import java.util.HashSet
import arcbasis._symboltable.PortSymbol
import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.xtend.Adapter
import montithings.generator.codegen.ConfigParams
import cdlangextension._ast.ASTCDEImportStatement-->

  <#macro printIncludes comp config>
  <#import "/template/adapter/Header.ftl" as Adapter>
      <#list portIncludes as include >
 include
 </#list>
    <#assign isList = includeStatements.toList()>
	<@Adapter.printIncludes isList/>
  </#macro>

  <#macro printVars comp, ports, config>
    // Ports
    <#list ports as port>
    <#assign type = ComponentHelper.getRealPortCppTypeString(port.component.get, port, config)>
    <#assign name = port.name>
    InOutPort<${type}>* ${name} = new InOutPort<${type}>();
    </#list>

    <#if comp.isDecomposed>
    // Internal monitoring of ports (for pre- and postconditions of composed components)
    <#list ports as port>
    <#assign name = port.name>
    sole::uuid portMonitorUuid${name.toFirstUpper} = sole::uuid4 ();
    </#list>
    </#if>
</#macro>

<#macro printMethodHeaders ports, config>
    <#list ports as port>
    <#assign type = ComponentHelper.getRealPortCppTypeString(port.component.get, port, config)>
    <#assign name = port.name>
    InOutPort<${type}>* getPort${name.toFirstUpper}();
    void addInPort${name.toFirstUpper}(Port<${type}>* ${name});
    void removeInPort${name.toFirstUpper}(Port<${type}>* ${name});
    void addOutPort${name.toFirstUpper}(Port<${type}>* ${name});
    void removeOutPort${name.toFirstUpper}(Port<${type}>* ${name});
    </#list>
</#macro>

<#macro printMethodBodies ports, comp, compname, config>
    <#list ports as port>
    <#assign type = ComponentHelper.getRealPortCppTypeString(port.component.get, port, config)>
    <#assign name = port.name>
    ${Utils.printTemplateArguments(comp)}
    InOutPort<${type}>* ${compname}${Utils.printFormalTypeParameters(comp)}::getPort${name.toFirstUpper}(){
      return ${name};
    }

    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp)}::addInPort${name.toFirstUpper}(Port<${type}>* port){
      ${name}->getInport ()->addManagedPort (port);
    }

    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp)}::removeInPort${name.toFirstUpper}(Port<${type}>* port){
      ${name}->getInport ()->removeManagedPort (port);
    }

    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp)}::addOutPort${name.toFirstUpper}(Port<${type}>* port){
      ${name}->getOutport ()->addManagedPort (port);
    }

    ${Utils.printTemplateArguments(comp)}
    void ${compname}${Utils.printFormalTypeParameters(comp)}::removeOutPort${name.toFirstUpper}(Port<${type}>* port){
      ${name}->getOutport ()->removeManagedPort (port);
    }
    </#list>
</#macro>
