# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.util

import java.io.File
import java.util.List
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.ConfigParams-->

  def static printCMakeForSubdirectories(List<String> subdirectories) {
    return '''
    cmake_minimum_required (VERSION 3.8)
    project ("MontiThings Application")
    <#list subdirectories as subdir >
 add_subdirectory ("${subdir}")
 </#list>
    '''
  }