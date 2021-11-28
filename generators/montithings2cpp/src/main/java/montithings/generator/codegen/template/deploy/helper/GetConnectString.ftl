<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "interfaceName", "prettyprint")}
<#include "/template/Preamble.ftl">

<#if prettyprint>
  std::string str = cmp.getConnectionString${interfaceName}();
<#else>
  json j = json::parse(cmp.getConnectionString${interfaceName}());
  std::string str = json::parse(j.dump(0)).dump();
  str = std::regex_replace(str, std::regex("\""), "\\\"");
  str = "\"" + str + "\"";
</#if>