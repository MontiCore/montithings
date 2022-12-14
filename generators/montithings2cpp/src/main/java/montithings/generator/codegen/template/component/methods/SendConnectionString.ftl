<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::sendConnectionString(std::string connectionStringTopic, std::string connectionString){
  LOG (DEBUG) << "Sending connection string " << connectionString << " to topic "
              << connectionStringTopic;

  mqttClientLocalInstance->publishRetainedMessage (connectionStringTopic, connectionString);
}