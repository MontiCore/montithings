<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">
${Identifier.getBehaviorImplName()}(${compname}Impl${Utils.printFormalTypeParameters(comp, false)}(instanceName, *this, ${Identifier.getStateName()}, ${Identifier.getInterfaceName()}))