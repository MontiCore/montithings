<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/logtracing/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

${Identifier.createInstance(comp)}

#pragma once

#include "EventObserver.h"
#include "${compname}Input${Utils.printFormalTypeParameters(comp)}.h"

${Utils.printNamespaceStart(comp)}

class ${compname}; // forward declaration to avoid cyclic include

${Utils.printTemplateArguments(comp)}
class ${className} : public EventObserver {

private:
    ${comp.getName()}* comp;

public:
    ${className}(${compname} *comp);

    ~${className}() = default;

    void checkInput (${compname}Input ${Identifier.getInputName()});

    void checkOutput ();

    void onEvent () override;
};

${Utils.printNamespaceEnd(comp)}
