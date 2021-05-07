<#-- (c) https://github.com/MontiCore/monticore-->
${tc.signature("comp","config","port")}
<#include "/template/Preamble.ftl">

<#if config.getLogTracing().toString() == "ON">
    std::pair${"<"}sole::uuid, tl::optional${"<"}${ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)}${">>"} ${port.getName()}Wrapped = montithings::logtracer::utils::piggypackId${"<"}tl::optional${"<"}${ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)}${">>"}(result.get${port.getName()?cap_first}());
</#if>