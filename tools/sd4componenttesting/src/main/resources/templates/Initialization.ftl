<#macro printInitialization mainComp>
// initialization
#include "easyloggingpp/easylogging++.h"
#include "gtest/gtest.h"
#include <chrono>
#include <thread>

#include "${mainComp.getName()}.h"

<#assign typeList = {}>
<#list mainComp.getSubComponents() as component>
  <#assign compTypeName = component.getType().getName()>
    <#if !typeList[compTypeName]?? >
#include "${compTypeName}.h"
      <#assign typeList = typeList + {compTypeName : 0}>
    </#if>
</#list>

INITIALIZE_EASYLOGGINGPP
</#macro>
