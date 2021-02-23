<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "statement", "config", "number", "isPrecondition", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/prepostconditions/SpecificPreamble.ftl">

#pragma once

#include "${compname}${prefix}condition.h"
#include ${"<regex>"}

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${className} : public ${compname}${prefix}condition${generics}
{
public:
using ${compname}${prefix}condition::${compname}${prefix}condition;
bool check (${compname}State${generics} state, ${compname}Input${generics} input <#if !isPrecondition>, ${compname}Result${generics} result</#if>) const override;
void resolve (${compname}State${generics} &state, ${compname}Input${generics} &input <#if !isPrecondition>, ${compname}Result${generics} &result</#if>) override;
std::string toString () const override;
bool isCatched () override;
};

<#if Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.prepostconditions.SpecificBody", [comp, statement, config, number, isPrecondition, existsHWC])}
</#if>

${Utils.printNamespaceEnd(comp)}