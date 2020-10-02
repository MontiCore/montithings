# (c) https://github.com/MontiCore/monticore
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#--package montithings.generator.codegen.xtend

import cdlangextension._ast.ASTCDEImportStatement
import java.util.HashSet
import java.util.List
import montithings.generator.codegen.ConfigParams
import montithings.generator.helper.ComponentHelper-->
	
	<#macro generateHeader packageName compname config >
#pragma once
#include <string>
#include "Port.h"
#include <map>
#include <vector>
#include <list>
#include <set>
#include <utility>
#include "tl/optional.hpp"
<@printIncludes(ComponentHelper.getImportStatements(compname,config))/>

${printNamespaceStart(packageName)}

class ${compname}AdapterTOP
{
private:
public:
	${compname}AdapterTOP() = default;
	<#list ComponentHelper.getImportStatements(compname,config) as importStatement >
	virtual ${printCDType(importStatement)} convert(${importStatement.importClass} element) = 0;
	virtual ${importStatement.importClass} convert(${printCDType(importStatement)} element) = 0;
	</#list>
};
${printNamespaceEnd(packageName)}
</#macro>
	
	<#macro  printIncludes imports >
	<#assign portIncludes = tc.instantiate("java.util.HashSet")>
	<#list imports as importStatement >
    	<#assign portPackage = importStatement.getImportSource().toString()>
    	portIncludes.add("#include ${portPackage}");
      	portIncludes.add("#include \"${printCDType(importStatement).replace("::", "/")}.h\"")
	</#list>
	<#list portIncludes as include >
 include
 </#list>
</#macro>

<#macro printCDType importStatement>
		<#assign StringBuilder = tc.instantiate("java.util.StringBuilder")>
  		<#assign namespace = StringBuilder("montithings::")>
    	if(importStatement.isPresentPackage()){
    	<#assign packages = importStatement.getPackage().getPartList()>
    	
    	for (String packageName : packages) {
      	namespace.append(packageName).append("::");
    	}
    	return ComponentHelper.printPackageNamespaceFromString(packages.get(packages.size-1)+"::"+importStatement.getName().toString(), namespace.toString());
    	}
        return importStatement.getName().toString();
</#macro>

<#macro printNamespaceStart packages >
      	namespace montithings {
      	<#list 0..<packages.size as i >
 namespace ${packages.get(i)} {
 </#list>
</#macro>

<#macro printNamespaceEnd packages >
    	<#list 0..<packages.size as i >
 } // namespace ${packages.get(packages.size - (i+1))}
 </#list>
    	} // namespace montithings
</#macro>