<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname")}
<#include "/template/component/helper/GeneralPreamble.ftl">
${Identifier.getBehaviorImplName()}(${compname}Impl${Utils.printFormalTypeParameters(comp, false)}(instanceName, ${Identifier.getStateName()}, ${Identifier.getInterfaceName()}))