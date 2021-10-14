<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/impl/helper/GeneralPreamble.ftl">
<#assign isLogTracingEnabled = config.getLogTracing().toString() == "ON">

${Utils.printTemplateArguments(comp)}
${compname}Result${generics} ${className}${generics}::init(${compname}Input${generics}
${Identifier.getInputName()}){
${compname}Result${generics} ${Identifier.getResultName()};
${compname}State${generics} state__at__pre = ${Identifier.getStateName()};

${tc.includeArgs("template.impl.helper.RecorderComputationMeasurementStart", [comp, config])}

${ComponentHelper.printJavaBlock(ComponentHelper.getInitBehavior(comp), isLogTracingEnabled)}

${tc.includeArgs("template.impl.helper.RecorderComputationMeasurementEnd", [comp, config])}
return ${Identifier.getResultName()};
}