<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("packageName", "compname", "config", "existsHWC")}
<#include "/template/adapter/helper/GeneralPreamble.ftl">

#include "${className}.h"

${tc.includeArgs("template.adapter.helper.NamespaceStart", [packageName])}

// intentionally left empty - all methods need to implemented with hand-written code using TOP mechanism

${tc.includeArgs("template.adapter.helper.NamespaceEnd", [packageName])}
