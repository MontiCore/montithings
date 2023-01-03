<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::sendConnectionString(std::string connectionStringTopic, std::string connectionString)
{
  LOG (DEBUG) << "Sending connection string " << connectionString
              << " of instance ${comp.getFullName()} to topic " << connectionStringTopic;
  
  json j;
  j["instanceName"] = "${comp.getFullName()}";
  <#list ComponentHelper.getInterfaceClassNames(comp)[0..*1] as interface>
  j["interface"] = "${interface}";
  </#list>
  j["connectionString"] = connectionString;
  std::string message = j.dump ();

  mqttClientLocalInstance->publishRetainedMessage (connectionStringTopic, connectionString);
}