<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "isPrecondition" "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign compname = comp.getName()>
<#assign prefix = "">
<#if isPrecondition>
  <#assign prefix = "Pre">
<#else>
  <#assign prefix = "Post">
</#if>
<#assign className = compname + prefix + "condition">
<#if existsHWC>
    <#assign className += "TOP">
</#if>
    
#pragma once
#include "${compname}Input.h"
<#if !isPrecondition>
  #include "${compname}Result.h"
</#if>
#include "${compname}State.h"
#include "easyloggingpp/easylogging++.h"

${Utils.printNamespaceStart(comp)}

class ${className}
{
protected:
std::string instanceName;
public:
${className} (const std::string &instanceName);
virtual bool isCatched ();
virtual bool check (${compname}State state, ${compname}Input input <#if !isPrecondition>, ${compname}Result result</#if>) const = 0;
virtual std::string toString () const = 0;
virtual void resolve (${compname}State &state, ${compname}Input &input <#if !isPrecondition>, ${compname}Result &result</#if>) = 0;
void apply (${compname}State &state, ${compname}Input &input <#if !isPrecondition>, ${compname}Result &result</#if>);
void logError (${compname}State state, ${compname}Input input <#if !isPrecondition>, ${compname}Result result</#if>) const;
};

${Utils.printNamespaceEnd(comp)}