<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#import "/template/behavior/implementation/ImplementationFile.ftl" as ImplementationFile>

<#assign generics = Utils.printFormalTypeParameters(comp)>
#pragma once
#include "${compname}Input.h"
#include "${compname}Result.h"
#include "IComputable.h"
#include ${"<stdexcept>"}
#include "json/json.hpp"
#include ${"<Utils.h>"}
#include ${"<fstream>"}

using json = nlohmann::json;

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${compname}Impl<#if existsHWC>TOP</#if>
: public IComputable<${compname}Input${generics},${compname}Result${generics}>{

protected:
std::string instanceName;
${Utils.printVariables(comp)}
<#-- Currently useless. MontiArc 6's getFields() returns both variables and parameters -->
<#-- ${Utils.printConfigParameters(comp)} -->
public:
${tc.includeArgs("template.behavior.implementation.printConstructor", [comp, compname, existsHWC])}

void setInstanceName (const std::string &instanceName);

virtual void storeState ();
virtual bool restoreState ();

<#if ComponentHelper.hasBehavior(comp)>
    ${compname}Result${generics} getInitialValues() override;
    ${compname}Result${generics} compute(${compname}Input${generics} input) override;
<#else>
    ${compname}Result${generics} getInitialValues() = 0;
    ${compname}Result${generics} compute(${compname}Input${generics} input) = 0;
</#if>
};

<#if Utils.hasTypeParameter(comp)>
    ${tc.includeArgs("template.behavior.implementation.generateImplementationBody", [comp, compname, existsHWC])}
</#if>
${Utils.printNamespaceEnd(comp)}
