<#-- (c) https://github.com/MontiCore/monticore -->
<#include "/template/ConfigPreamble.ftl">
<#include "/template/TcPreamble.ftl">
<#include "/template/CompPreamble.ftl">

<#assign generics = Utils.printFormalTypeParameters(comp)>
<#assign needsRunMethod = (ComponentHelper.isTimesync(comp) && !ComponentHelper.isApplication(comp, config))>
