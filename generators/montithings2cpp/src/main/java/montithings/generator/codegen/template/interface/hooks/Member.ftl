<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}
<#include "/template/interface/helper/GeneralPreamble.ftl">
<#assign generics = Utils.printFormalTypeParameters(comp)>
${compname}Interface${generics} ${Identifier.getInterfaceName()};