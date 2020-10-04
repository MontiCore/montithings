<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("subdirectories")}

cmake_minimum_required (VERSION 3.8)
project ("MontiThings Application")
<#list subdirectories as subdir >
  add_subdirectory ("${subdir}")
</#list>