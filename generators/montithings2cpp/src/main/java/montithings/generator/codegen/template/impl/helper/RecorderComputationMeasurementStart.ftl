<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("comp", "config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if config.getRecordingMode().toString() == "ON">
    auto timeStartCalc = std::chrono::high_resolution_clock::now();
</#if>
