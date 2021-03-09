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
  <#if ComponentHelper.isArcField(var)>
    vectorOf__${varName?cap_first}.push_back(std::make_pair(std::chrono::system_clock::now(), ${varName}));
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

  ${Utils.printTemplateArguments(comp)}
  ${type} ${className}${generics}::agoGet${varName?cap_first}(const std::chrono::nanoseconds ago_time)
  {
  auto now = std::chrono::system_clock::now();
  int i = 1;
  while (i <= vectorOf__${varName?cap_first}.size()){
  if(vectorOf__${varName?cap_first}.at(vectorOf__${varName?cap_first}.size() - i).first < now-ago_time){
  return vectorOf__${varName?cap_first}.at(vectorOf__${varName?cap_first}.size() - i).second;
  }
  i++;
  }
  return vectorOf__${varName?cap_first}.at(0).second;
  }
</#list>

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::setInstanceName (const std::string &instanceName)
{
this->instanceName = instanceName;
}

${tc.includeArgs("template.state.printRestoreMethods", [comp, className])}