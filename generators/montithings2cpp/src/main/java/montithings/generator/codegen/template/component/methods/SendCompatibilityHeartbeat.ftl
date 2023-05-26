<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::sendCompatibilityHeartbeat(std::future<void> keepAliveFuture){
  while (!hasComputedTODO) {

    json j;
    j["component_name"] = this->getInstanceName();
    j["component_type"] = "<#list ComponentHelper.getInterfaceClassNames(comp)[0..*1] as interface>${interface}</#list>";
    j["connection_string"] = getConnectionStringCo${compname}();

    <#if ComponentHelper.getIncomingPortsToTest(comp)?size <= 0>
      j["requirements"] = "[]";
    <#else>
      <#list comp.getAllIncomingPorts()[0..*1] as p>
        j["requirements"] = "[${p.getType().print()}]";
      </#list>
    </#if>

    mqttClientCompatibilityInstance->publish("component_offer", j.dump());
    std::this_thread::sleep_for(std::chrono::seconds(45));
  }
}