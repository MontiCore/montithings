<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cppFileNames", "subDirNames", "existsHWC")}
project(sd4ctests)

enable_testing()

<#list subDirNames as subDirName>
    add_subdirectory ("${subDirName}")
</#list>

<#list cppFileNames as cppFileName>
    package_add_test(${cppFileName?string?remove_ending(".cpp")}TestSuite ${cppFileName})
</#list>
