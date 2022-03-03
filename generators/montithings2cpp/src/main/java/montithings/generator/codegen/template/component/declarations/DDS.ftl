<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if brokerIsDDS>
  // sensor actuator ports require cmd args in order to set up their DDS clients
  int argc;
  char *argv;
</#if>