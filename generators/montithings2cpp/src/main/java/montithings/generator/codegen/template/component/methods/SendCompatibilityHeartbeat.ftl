<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::sendCompatibilityHeartbeat(std::future<void> keepAliveFuture){
  bool first = true;
  std::string ip_address = "";
  while (true) {
    if (first || !mqttClientCompatibilityInstance->isConnected()) {
      try {
        mqttClientCompatibilityInstance = MqttClient::localInstance("192.168.0.10", 1883);
        mqttClientCompatibilityInstance->addUser(this);
        mqttClientCompatibilityInstance->subscribe("/component_match");
        mqttClientCompatibilityInstance->subscribe("/offered_ip");
        ip_address = getIPAddress();
        first = false;
      } catch (std::runtime_error &err) {
        log("Can't connect to compatibility-broker yet, Trying again in 3 seconds!");
        std::this_thread::sleep_for(std::chrono::seconds(3));
        continue;
      }
    }

    if (!hasComputedTODO) {
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
      j["ipaddress"] = ip_address;
      mqttClientCompatibilityInstance->publish("component_offer", j.dump());
    }
    std::this_thread::sleep_for(std::chrono::seconds(45));
  }
}