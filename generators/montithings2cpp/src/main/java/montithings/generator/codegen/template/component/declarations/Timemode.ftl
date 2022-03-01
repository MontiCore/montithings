<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

TimeMode timeMode =
<#if ComponentHelper.isTimesync(comp)>
  TIMESYNC
<#else>
  EVENTBASED
</#if>;