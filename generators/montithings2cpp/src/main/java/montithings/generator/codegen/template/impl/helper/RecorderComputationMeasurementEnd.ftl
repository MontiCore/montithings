<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("comp", "config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#if recordingEnabled>
    if (montithings::library::hwcinterceptor::isRecording)
    {
        auto timeEndCalc = std::chrono::high_resolution_clock::now();
        auto latency = timeEndCalc - timeStartCalc;
        montithings::library::hwcinterceptor::storeCalculationLatency(latency.count());
    }
</#if>
