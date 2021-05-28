<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/component/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

${Identifier.createInstance(comp)}

#pragma once

#include "${className}.h"

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${className}LogTraceObserver : public EventObserver {

private:
    ${comp.getName()}* comp;

    void onEvent () override;

public:
    ${className}LogTraceObserver(${className} *comp);

    ~${className}LogTraceObserver() = default;
};

${Utils.printNamespaceEnd(comp)}
