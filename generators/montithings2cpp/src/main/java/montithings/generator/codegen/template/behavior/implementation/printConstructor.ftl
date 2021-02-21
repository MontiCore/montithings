<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${className}(${compname}State& state) : ${Identifier.getStateName()}(state) {}