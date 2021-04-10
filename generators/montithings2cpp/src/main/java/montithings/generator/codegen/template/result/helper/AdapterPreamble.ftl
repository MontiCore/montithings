<#include "/template/result/helper/GeneralPreamble.ftl">
<#assign cdeImportStatementOpt = ComponentHelper.getCDEReplacement(port, config)>
<#assign fullImportStatemantName = cdeImportStatementOpt.get().getSymbol().getFullName()?split(".")>
<#assign adapterName = fullImportStatemantName[0]+"Adapter">
<#assign cdSimpleName = cdeImportStatementOpt.get().getSymbol().getFullName()?keep_after_last(".")>
