<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if recordingEnabled>
    auto timeStartCalc = std::chrono::high_resolution_clock::now();
</#if>
