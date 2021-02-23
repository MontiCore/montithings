<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "isPrecondition", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/prepostconditions/GeneralPreamble.ftl">
    
#pragma once
#include "${compname}Input.h"
<#if !isPrecondition>
  #include "${compname}Result.h"
</#if>
#include "${compname}State.h"
#include "easyloggingpp/easylogging++.h"

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${className}
{
protected:
std::string instanceName;
public:
${className} (const std::string &instanceName);
virtual bool isCatched ();
virtual bool check (${compname}State${generics} state, ${compname}Input${generics} input <#if !isPrecondition>, ${compname}Result${generics} result</#if>) const = 0;
virtual std::string toString () const = 0;
virtual void resolve (${compname}State${generics} &state, ${compname}Input${generics} &input <#if !isPrecondition>, ${compname}Result${generics} &result</#if>) = 0;
void apply (${compname}State${generics} &state, ${compname}Input${generics} &input <#if !isPrecondition>, ${compname}Result${generics} &result</#if>);
void logError (${compname}State${generics} state, ${compname}Input${generics} input <#if !isPrecondition>, ${compname}Result${generics} result</#if>) const;
};

<#if Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.prepostconditions.GeneralBody", [comp, config, isPrecondition, existsHWC])}
</#if>
${Utils.printNamespaceEnd(comp)}