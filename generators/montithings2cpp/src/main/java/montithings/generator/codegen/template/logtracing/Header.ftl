<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/component/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

${Identifier.createInstance(comp)}

#pragma once

#include "EventObserver.h"
#include "${compname}Input${Utils.printFormalTypeParameters(comp)}.h"

${Utils.printNamespaceStart(comp)}

class ${className}; // forward declaration to avoid cyclic include

${Utils.printTemplateArguments(comp)}
class ${className}LogTraceObserver : public EventObserver {

private:
    ${comp.getName()}* comp;

public:
    ${className}LogTraceObserver(${className} *comp);

    ~${className}LogTraceObserver() = default;

    void checkInput (${compname}Input${Utils.printFormalTypeParameters(comp)} ${Identifier.getInputName()});

    void checkOutput ();

    void onEvent () override;
};

${Utils.printNamespaceEnd(comp)}
