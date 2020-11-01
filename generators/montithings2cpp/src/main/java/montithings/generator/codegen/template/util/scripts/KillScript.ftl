<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("components")}

<#list components as comp >
  killall ${comp}
</#list>