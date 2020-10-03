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