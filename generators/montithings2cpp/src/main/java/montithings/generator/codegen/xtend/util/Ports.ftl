# (c) https://github.com/MontiCore/monticore
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

class Ports {

  def static String printIncludes(ComponentTypeSymbol comp, ConfigParams config) {
  <#assign HashSet<String> portIncludes = new HashSet<String>()>
  	<#assign HashSet<ASTCDEImportStatement> includeStatements = new HashSet<ASTCDEImportStatement>()>
    for (port : comp.ports) {
      if (ComponentHelper.portUsesCdType(port)) {
          <#assign cdeImportStatementOpt = ComponentHelper.getCppImportExtension(port, config);>
            if(cdeImportStatementOpt.isPresent()) {
              includeStatements.add(cdeImportStatementOpt.get());
              <#assign portNamespace = ComponentHelper.printCdPortPackageNamespace(comp, port, config)>
              <#assign adapterName = portNamespace.split("::");>
                 if(adapterName.length>=2) {
                    portIncludes.add('''#include "${adapterName.get(adapterName.length-2)}Adapter.h"''')
                 }
            }
            else{
    		    <#assign portNamespace = ComponentHelper.printCdPortPackageNamespace(comp, port, config)>
      		    portIncludes.add('''#include "${portNamespace.replace("::", "/")}.h"''')
      		}
      	}
      }
	return '''
	<#list portIncludes as include >
 include
 </#list>
	${Adapter.printIncludes(includeStatements.toList)}
	'''
  }

  def static printVars(ComponentTypeSymbol comp, Collection<PortSymbol> ports, ConfigParams config) {
  return	'''
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
    '''
  }

  def static printMethodHeaders(Collection<PortSymbol> ports, ConfigParams config){
  return	'''
    <#list ports as port>
    <#assign type = ComponentHelper.getRealPortCppTypeString(port.component.get, port, config)>
    <#assign name = port.name>
    InOutPort<${type}>* getPort${name.toFirstUpper}();
    void addInPort${name.toFirstUpper}(Port<${type}>* ${name});
    void removeInPort${name.toFirstUpper}(Port<${type}>* ${name});
    void addOutPort${name.toFirstUpper}(Port<${type}>* ${name});
    void removeOutPort${name.toFirstUpper}(Port<${type}>* ${name});
    </#list>
    '''
  }

  def static printMethodBodies(Collection<PortSymbol> ports, ComponentTypeSymbol comp, String compname, ConfigParams config){
  return	'''
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
    '''
    }
}