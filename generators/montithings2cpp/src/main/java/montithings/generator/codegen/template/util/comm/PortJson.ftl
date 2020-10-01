# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentTypeSymbol
import arcbasis._symboltable.ComponentInstanceSymbol
import arcbasis._symboltable.PortSymbol
import arcbasis._ast.ASTConnector
import arcbasis._ast.ASTPortAccess
import montithings._ast.ASTMTComponentType
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.ConfigParams-->

  def static printPortJson(ComponentTypeSymbol comp, ConfigParams config) {
    printPortJson(comp, config, comp.fullName)
  }

  def static printPortJson(ComponentTypeSymbol comp, ConfigParams config, String prefix) {
    return '''
    {
    ${FOR subcomp : comp.subComponents SEPARATOR ","}
      "${subcomp.name}": {
        "management": "${config.componentPortMap.getManagementPort(prefix + "." + subcomp.name)}",
        "communication": "${config.componentPortMap.getCommunicationPort(prefix + "." + subcomp.name)}"
      }
    </#list>
    }
    '''
  }