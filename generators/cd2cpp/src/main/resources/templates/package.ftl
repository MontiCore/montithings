<#-- (c) https://github.com/MontiCore/monticore -->
// (c) https://github.com/MontiCore/monticore
${tc.signature("imports")}

<#list imports as import>
#include "${import}.h"
</#list>