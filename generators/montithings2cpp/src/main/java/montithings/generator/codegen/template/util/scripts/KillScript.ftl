# (c) https://github.com/MontiCore/monticore
${tc.signature("components")}
<#list components as comp >
  killall ${comp}
</#list>