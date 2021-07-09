<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "useWsPorts", "existsHWC")}
<#include "/template/component/helper/GeneralPreamble.ftl">

#include "IComponent.h"
#include "Port.h"
#include "InOutPort.h"
<#list comp.getPorts() as port>
    <#assign addPort = GeneratorHelper.getPortHwcTemplateName(port, config)>
    <#if config.getTemplatedPorts()?seq_contains(port) && addPort!="Optional.empty">
      #include "${Names.getSimpleName(addPort.get())?cap_first}.h"
    </#if>
</#list>
#include ${"<string>"}
#include ${"<map>"}
#include ${"<vector>"}
#include ${"<list>"}
#include ${"<set>"}
#include ${"<thread>"}
#include "sole/sole.hpp"
#include "easyloggingpp/easylogging++.h"
#include ${"<iostream>"}
<#if config.getMessageBroker().toString() == "MQTT">
  #include "MqttClient.h"
  #include "MqttPort.h"
  #include "Utils.h"
</#if>
${Utils.printIncludes(comp, config)}
${tc.includeArgs("template.prepostconditions.hooks.Include", [comp])}
${tc.includeArgs("template.interface.hooks.Include", [comp])}
${tc.includeArgs("template.state.hooks.Include", [comp])}
${tc.includeArgs("template.logtracing.hooks.Include", [comp, config])}

<#if comp.isDecomposed()>
    ${Utils.printIncludes(comp, compname, config)}
<#else>
  #include "${compname}Impl.h"
    ${tc.includeArgs("template.input.hooks.Include", [comp])}
    ${tc.includeArgs("template.result.hooks.Include", [comp])}
</#if>
<#if config.getRecordingMode().toString() == "ON">
  #include "record-and-replays/recorder/HWCInterceptor.h"
</#if>