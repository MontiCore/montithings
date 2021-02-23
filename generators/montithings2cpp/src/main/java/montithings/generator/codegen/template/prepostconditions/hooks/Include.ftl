<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}
<#assign compname = comp.getName()>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

#include "${compname}Precondition.h"
#include "${compname}Postcondition.h"

<#assign preconditions = ComponentHelper.getPreconditions(comp)>
<#if preconditions?size gt 0>
    <#list 1..preconditions?size as i>
      #include "${compname}Precondition${i}.h"
    </#list>
</#if>
<#assign postconditions = ComponentHelper.getPostconditions(comp)>
<#if postconditions?size gt 0>
    <#list 1..postconditions?size as i>
      #include "${compname}Postcondition${i}.h"
    </#list>
</#if>