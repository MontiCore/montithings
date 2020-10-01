# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend

import cdlangextension._ast.ASTCDEImportStatement
import java.util.HashSet
import java.util.List
import montithings.generator.codegen.ConfigParams
import montithings.generator.helper.ComponentHelper-->

	def static generateCpp(List<String> packageName, String compname, ConfigParams config) {
    return '''
    #include "${compname}AdapterTOP.h"
    ${printNamespaceStart(packageName)}
    ${printNamespaceEnd(packageName)}
    '''
  }