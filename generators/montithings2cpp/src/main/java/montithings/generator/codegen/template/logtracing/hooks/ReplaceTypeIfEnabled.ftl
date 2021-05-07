<#-- (c) https://github.com/MontiCore/monticore-->
${tc.signature("comp","config","type")}
<#if config.getLogTracing().toString() == "ON">
    std::pair${"<"}sole::uuid, tl::optional${"<"}${type}${">>"}
<#else>
    ${type}
</#if>