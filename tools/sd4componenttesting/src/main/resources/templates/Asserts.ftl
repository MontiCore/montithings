<#-- (c) https://github.com/MontiCore/monticore -->
<#macro printAsserts ast prettyPrinter cppPrettyPrinter componentHelper mainComp mainCompName>
<#import "/templates/asserts/MainInput.ftl" as mainInput>
<#import "/templates/asserts/MainOutput.ftl" as mainOutput>
<#import "/templates/asserts/Expression.ftl" as expression>
<#import "/templates/asserts/Delay.ftl" as delay>

  // asserts:
<#assign testDiagramSymbol = ast.getEnclosingScope().getDiagramSymbols().values()[0]>
<#assign testDiagramComp = testDiagramSymbol.getAstNode()>
<#assign refCounterList = {}>
<#list testDiagramComp.getSD4CElementList() as sD4CElement>
  <#if sD4CElement.getType() == "MAIN_INPUT">
    <#-- print main input assert -->
    <@mainInput.printMainInput sD4CElement mainComp mainCompName />
  <#elseif sD4CElement.getType() == "MAIN_OUTPUT">
    <#-- print main input assert -->
    <@mainOutput.printMainOutput sD4CElement mainCompName />
  <#elseif sD4CElement.getType() == "EXPRESSION">
    <#-- print expression assert -->
    <@expression.printExpression prettyPrinter cppPrettyPrinter sD4CElement />
  <#elseif sD4CElement.getType() == "DELAY">
    <#-- print delay assert -->
    <@delay.printDelay componentHelper prettyPrinter sD4CElement />
  <#elseif sD4CElement.getType() == "DEFAULT">
    <#-- print default assert -->
    <#list sD4CElement.getTargetList() as portAccess>
      <#if portAccess.isPresentComponent()>
  // check input of Target ${portAccess.getComponent()}.${portAccess.getPort()}
        <#assign compName = portAccess.getComponent()>
        <#assign compTypeName = mainComp.getSubComponent(portAccess.getComponent()).get().getType().getName()>
      <#else>
  // check input of Target ${portAccess.getPort()}
        <#assign compName = "">
        <#assign compTypeName = mainCompName>
      </#if>
      <#assign portName = portAccess.getPort()?cap_first>
      <#if !refCounterList["portSpy" + compTypeName + compName + portName]?? >
        <#assign refCounterList = refCounterList + {"portSpy" + compTypeName + compName + portName : 0}>
      <#else >
        <#assign refCounterList = refCounterList + {"portSpy" + compTypeName + compName + portName : (refCounterList["portSpy" + compTypeName + compName + portName] + 1)}>
      </#if>
  LOG(INFO) << "check ${prettyPrinter.prettyprint(sD4CElement)?replace("\n", "")?replace("\r", "")}";
  ASSERT_TRUE (portSpy${compTypeName}${compName?cap_first}${portName}.getRecordedMessages().size() >= ${refCounterList["portSpy" + compTypeName + compName + portName] + 1});
  ASSERT_TRUE (portSpy${compTypeName}${compName?cap_first}${portName}.getRecordedMessages().at(${refCounterList["portSpy" + compTypeName + compName + portName]}).has_value());
      <#if sD4CElement.getValueList()?size < 2 >
  EXPECT_EQ (portSpy${compTypeName}${compName?cap_first}${portName}.getRecordedMessages().at(${refCounterList["portSpy" + compTypeName + compName + portName]}).value().getPayload(), ${sD4CElement.getValue(0).getValue()});
      <#else>
  EXPECT_EQ (portSpy${compTypeName}${compName?cap_first}${portName}.getRecordedMessages().at(${refCounterList["portSpy" + compTypeName + compName + portName]}).value().getPayload(), ${sD4CElement.getValue(portAccess?index).getValue()});
      </#if>

    </#list>
  </#if>
</#list>
</#macro>
