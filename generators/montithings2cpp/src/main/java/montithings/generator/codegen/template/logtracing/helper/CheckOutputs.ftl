<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/logtracing/helper/GeneralPreamble.ftl">

<#list comp.getOutgoingPorts() as p>
  <#assign type = TypesPrinter.getRealPortCppTypeString(comp, p, config)>

  if(comp->getInterface()->getPort${p.getName()?cap_first}()->hasValue(this->uuid)) {
    isOutputPresent = true;

    <#list comp.getAstNode().getConnectors() as connector>
      <#list connector.getTargetList() as target>
        <#if target.getQName() == p.getName()>
          Message<${type}> valuePort${p.getName()?cap_first} = comp->getInterface()->getPort${p.getName()?cap_first}()->getCurrentValue(this->uuid, false).value();
          comp->getLogTracer()->getCurrTraceOutput().addTrace(valuePort${p.getName()?cap_first}.getUuid(), "${p.getName()}");

          /*for (TraceInput& traceInput : comp->getLogTracer()->getCurrInputGroup()) {
            ${className}Input input = jsonToData<${className}Input>(traceInput.getSerializedInput());
            input.set${p.getName()?cap_first}(valuePort${p.getName()?cap_first});
            traceInput.setSerializedInput(dataToJson(input));
          }*/
         </#if>
      </#list>
    </#list>
  }
</#list>