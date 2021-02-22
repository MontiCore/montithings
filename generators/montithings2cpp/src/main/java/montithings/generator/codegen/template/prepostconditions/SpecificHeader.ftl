<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "number", "isPrecondition", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign compname = comp.getName()>
<#assign prefix = "">
<#if isPrecondition>
    <#assign prefix = "Pre">
<#else>
    <#assign prefix = "Post">
</#if>
<#assign className = compname + prefix + "condition" + number>
<#if existsHWC>
    <#assign className += "TOP">
</#if>

#pragma once

#include "${compname}${prefix}condition.h"

${Utils.printNamespaceStart(comp)}

class ${className} : public ${compname}${prefix}condition
{
public:
using ${compname}${prefix}condition::${compname}${prefix}condition;
bool check (${compname}State state, ${compname}Input input <#if !isPrecondition>, ${compname}Result result</#if>) const override;
void resolve (${compname}State &state, ${compname}Input &input <#if !isPrecondition>, ${compname}Result &result</#if>) override;
std::string toString () const override;
bool isCatched () override;
};

${Utils.printNamespaceEnd(comp)}