<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","hostclassName")}
<#include "/template/TcPreamble.ftl">
<#include "/template/interface/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
${className}${generics}* ${hostclassName}${Utils.printFormalTypeParameters(comp)}::getInterface(){
return &${Identifier.getInterfaceName()};
}