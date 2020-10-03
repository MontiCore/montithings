# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.util

import java.util.List
import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.ConfigParams-->

class Scripts {

  def static printRunScript(ComponentTypeSymbol comp, ConfigParams config) {
        <#assign instances = ComponentHelper.getInstances(comp);>
    return '''
    <#list instances as pair >
 ./${pair.getKey().fullName} ${pair.getValue()} ${config.componentPortMap.getManagementPort(pair.getValue())} ${config.componentPortMap.getCommunicationPort(pair.getValue())} > ${pair.getValue()}.log 2>&1 &
 </#list>
    '''
  }

}