<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::sendConnectionString(std::string connectionStringTopic, std::string connectionString)
{
  LOG (DEBUG) << "Start sending connection string";
  
  json j;
  j["instanceName"] = "${comp.getFullName()}";
  <#if ComponentHelper.getInterfaceClassNames(comp)?size <= 0>
    j["interface"] = "Co${comp.getName()}";
  <#else>
    <#list ComponentHelper.getInterfaceClassNames(comp)[0..*1] as interface>
    j["interface"] = "${interface}";
    </#list>
  </#if>
  j["connectionString"] = connectionString;
  std::string message = j.dump ();

  mqttClientLocalInstance->publishRetainedMessage (connectionStringTopic, message);

  LOG (DEBUG) << "Sent connection string " << message << " of instance ${comp.getFullName()} to topic " 
              << connectionStringTopic;
}