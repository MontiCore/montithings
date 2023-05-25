<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::sendCompatibilityHeartbeat(std::future<void> keepAliveFuture){
  while (!hasComputedTODO) {
    mqttClientCompatibilityInstance->publish("component_offer", getConnectionStringCo${compname}());
    std::this_thread::sleep_for(std::chrono::seconds(45));
  }
}