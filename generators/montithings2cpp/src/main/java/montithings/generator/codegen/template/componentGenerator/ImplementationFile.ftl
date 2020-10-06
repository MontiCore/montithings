<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "config", "useWsPorts")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

#include "${compname}.h"
#include ${"<regex>"}
${Utils.printNamespaceStart(comp)}
<#if !comp.hasTypeParameter()>
    ${tc.includeArgs("template.componentGenerator.generateBody", [comp, compname, config])}
</#if>
${Utils.printNamespaceEnd(comp)}

<#--def protected static List<String> getInheritedParams(ComponentTypeSymbol component) {
  <#assign List<String> result = new ArrayList;>
  <#assign List<FieldSymbol> configParameters = component.getParameters();>
  if (component.isPresentParentComponent()) {
    <#assign ComponentTypeSymbolLoader superCompReference = component.getParent();>
    <#assign List<FieldSymbol> superConfigParams = superCompReference.getLoadedSymbol()>
    .getParameters();
    if (configParameters?has_content()) {
      for (<#assign i = 0; i < superConfigParams.size(); i++) {>
        result.add(configParameters.get(i).getName());
      }
    }
  }
  return result;
}-->