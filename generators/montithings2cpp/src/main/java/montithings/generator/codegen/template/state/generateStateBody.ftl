<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

<#assign generics = Utils.printFormalTypeParameters(comp)>
<#list ComponentHelper.getVariablesAndParameters(comp) as var>
  <#assign type = ComponentHelper.printCPPTypeName(var.getType(), comp, config)>
  <#assign varName = var.getName()>

  ${Utils.printTemplateArguments(comp)}
  ${type} ${className}${generics}::get${varName?cap_first}() const
  {
  return ${varName};
  }

  ${Utils.printTemplateArguments(comp)}
  void ${className}${generics}::set${varName?cap_first}(${type} ${varName})
  {
  ${className}${generics}::${varName} = ${varName};
  <#if ComponentHelper.isArcField(var) && ComponentHelper.hasAgoQualification(comp, var)>
    auto now = std::chrono::system_clock::now();
    dequeOf__${varName?cap_first}.push_back(std::make_pair(now, ${varName}));
    cleanDequeOf${varName?cap_first}(now);
  </#if>
  }

  ${Utils.printTemplateArguments(comp)}
  ${type} ${className}${generics}::preSet${varName?cap_first}(${type} ${varName})
  {
    set${varName?cap_first}(${varName});
    return get${varName?cap_first}();
  }

  ${Utils.printTemplateArguments(comp)}
  ${type} ${className}${generics}::postSet${varName?cap_first}(${type} ${varName})
  {
  ${type} beforeValue = get${varName?cap_first}();
  set${varName?cap_first}(${varName});
  return beforeValue;
  }
</#list>

<#list ComponentHelper.getArcFieldVariables(comp) as var>
  <#assign varName = var.getName()>
  <#assign type = ComponentHelper.printCPPTypeName(var.getType(), comp, config)>

  <#if ComponentHelper.hasAgoQualification(comp, var)>
  ${Utils.printTemplateArguments(comp)}
  ${type} ${className}${generics}::agoGet${varName?cap_first}(const std::chrono::nanoseconds ago_time)
  {
  auto now = std::chrono::system_clock::now();
  for (auto it = dequeOf__${varName?cap_first}.crbegin(); it != dequeOf__${varName?cap_first}.crend(); ++it)
  {
  if (it->first < now - ago_time)
  {
  return it->second;
  }
  }
  return dequeOf__${varName?cap_first}.front().second;
  }

  ${Utils.printTemplateArguments(comp)}
  void ${className}${generics}::cleanDequeOf${varName?cap_first}(std::chrono::time_point<std::chrono::system_clock> now)
  {
  while (dequeOf__${varName?cap_first}.size() > 1 && dequeOf__${varName?cap_first}.at (1).first < now - highestAgoOf__${varName?cap_first})
  {
  dequeOf__${varName?cap_first}.pop_front ();
  }
  }
  </#if>
</#list>

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::setInstanceName (const std::string &instanceName)
{
this->instanceName = instanceName;
}

${tc.includeArgs("template.state.printRestoreMethods", [comp, className])}