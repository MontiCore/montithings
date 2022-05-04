<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("imports", "existsHWC")}
<#include "/template/Copyright.ftl">
<#include "/template/Preamble.ftl">

#pragma once
<#list imports as import>
#include "${import}.h"
</#list>