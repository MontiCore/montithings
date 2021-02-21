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

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::setInstanceName (const std::string &instanceName)
{
this->instanceName = instanceName;
}

${tc.includeArgs("template.state.printRestoreMethods", [comp, className])}