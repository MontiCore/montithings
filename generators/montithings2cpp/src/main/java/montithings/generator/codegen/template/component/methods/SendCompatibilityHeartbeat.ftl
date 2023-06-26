<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::sendCompatibilityHeartbeat(std::future<void> keepAliveFuture){
  bool first = true;
  while (true) {
    if (first || !mqttClientCompatibilityInstance->isConnected()) {
      try {
        mqttClientCompatibilityInstance = new MqttClient("192.168.0.10", 1883);
        mqttClientCompatibilityInstance->addUser(this);
        mqttClientCompatibilityInstance->subscribe("/component_match/#");
        mqttClientCompatibilityInstance->subscribe("/offered_ip/#");
        ip_address = getIPAddress();
        first = false;
      } catch (std::runtime_error &err) {
        log("Can't connect to compatibility-broker yet, Trying again in 3 seconds!");
        std::this_thread::sleep_for(std::chrono::seconds(3));
        continue;
      }
    }
    <#if ComponentHelper.getPortsWithTestBlocks(comp)?size <= 0>
    if (!isConnectedToOtherComponent) {
      json j;
      j["component_name"] = this->getInstanceName();
      j["component_type"] = "<#list ComponentHelper.getInterfaceClassNames(comp)[0..*1] as interface>${interface}</#list>";
      j["connection_string"] = getConnectionStringCo${compname}();
      j["requirements"] = "[]";
      j["ipaddress"] = ip_address;
      mqttClientCompatibilityInstance->publish("component_offer", j.dump());
    }
    <#else>
    <#list ComponentHelper.getPortsWithTestBlocks(comp) as p>
    if (!mqttClientSenderInstance${p.getName()}->isConnected()) {
      isConnected${p.getName()} = false;
    }
    if (!isConnected${p.getName()}) {
      json j;
      j["component_name"] = this->getInstanceName();
      j["component_type"] = "<#list ComponentHelper.getInterfaceClassNames(comp)[0..*1] as interface>${interface}</#list>";
      j["connection_string"] = getConnectionStringCo${compname}();
      j["requirements"] = "[${p.getType().print()}]";
      j["ipaddress"] = ip_address;
      mqttClientCompatibilityInstance->publish("component_offer", j.dump());
    }
    </#list>
    </#if>
    std::this_thread::sleep_for(std::chrono::seconds(45));
  }
}