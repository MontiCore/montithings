// (c) https://github.com/MontiCore/monticore
<#import "/templates/Struct.ftl" as struct>
<#import "/templates/Initialization.ftl" as initialization>
<#import "/templates/Observer.ftl" as observer>
<#import "/templates/TestMethodInitialization.ftl" as testMethodInitialization>
<#import "/templates/Asserts.ftl" as asserts>

${tc.signature("prettyPrinter", "cppPrettyPrinter", "componentHelper")}

<#assign mainComp = ast.getEnclosingScope().getMainComponentTypeSymbol()>
<#assign mainCompName = mainComp.getName()>
<#assign package = "montithings">
<#list ast.getPackageDeclaration().getPartsList() as name>
  <#assign package = package + "::" + name>
</#list>
<#assign package = package + "::" >

<#-- initialization -->
<@initialization.printInitialization mainComp/>


<#-- struct for the test -->
<@struct.printStruct mainComp mainCompName package/>


<#-- observer -->
<@observer.printObserver mainComp package/>


<#-- Test method -->
TEST_F (${mainCompName}Test, ${ast.getTestDiagram().getName()})
{
  <#-- initialization of the test method -->
  <@testMethodInitialization.printTestMethodInitialization ast mainComp mainCompName/>


  <#-- asserts -->
  <@asserts.printAsserts ast prettyPrinter cppPrettyPrinter componentHelper mainComp mainCompName/>
}
