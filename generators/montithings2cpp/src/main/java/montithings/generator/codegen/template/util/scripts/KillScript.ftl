# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.util

import java.util.List
import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.ConfigParams-->

    def static printKillScript(List<String> components) {
    return '''
    <#list components as comp >
 killall ${comp}
 </#list>
    '''
  }
