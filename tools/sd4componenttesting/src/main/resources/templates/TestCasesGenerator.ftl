// (c) https://github.com/MontiCore/monticore
<#import "/templates/PortSpy.ftl" as portSpy>
#include "easyloggingpp/easylogging++.h"
#include "gtest/gtest.h"
#include <chrono>
#include <thread>

${tc.signature("prettyPrinter", "cppPrettyPrinter")}

<#assign mainComp = ast.getEnclosingScope().getMainComponentTypeSymbol()>
<#assign mainCompName = mainComp.getName()>
<#assign package = "montithings">
<#list ast.getPackageDeclaration().getPartsList() as name>
  <#assign package = package + "::" + name>
</#list>
<#assign package = package + "::" >

#include "${mainComp.getName()}.h"

<#assign typeList = {}>
<#list mainComp.getSubComponents() as component>
  <#assign compTypeName = component.getType().getName()>
    <#if !typeList[compTypeName]?? >
#include "${compTypeName}.h"
      <#assign typeList = typeList + {compTypeName : 0}>
    </#if>
</#list>

INITIALIZE_EASYLOGGINGPP

struct ${mainComp.getName()}Test : testing::Test
{
  ${package}${mainComp.getName()} *cmp${mainCompName};

<#list mainComp.getSubComponents() as component>
  ${package}${component.getType().getName()} *${component.getName()}Cmp;
  ${package}${component.getType().getName()}Impl *${component.getName()}Impl;
  ${package}${component.getType().getName()}State *${component.getName()}State;

</#list>

  ${ast.getTestDiagram().getName()} ()
  {
    cmp${mainCompName} = new ${package}${mainComp.getName()} ("${mainComp.getFullName()}");

<#list mainComp.getSubComponents() as component>
    ${component.getName()}Cmp = cmp${mainCompName}->getSubcomp__${component.getName()?cap_first}();
    ${component.getName()}Impl = ${component.getName()}Cmp->getImpl();
    ${component.getName()}State = ${component.getName()}Cmp->getState();

</#list>
  }

  ~${ast.getTestDiagram().getName()} ()
  {
    delete cmp${mainCompName};
  }
};


/**
 * This (abstract) class records all messages going through port.
 * The recorded messages can then be checked by the test case against the expected values.
 *
 * \tparam ComponentType class of the component this spy is attached to
 * \tparam PortType typename of the messages going through the port
 */
template <typename ComponentType, typename PortType> class PortSpy : public EventObserver
{
protected:
  ComponentType *component;
  std::vector<tl::optional<Message<PortType>>> recordedMessages;

public:
  explicit PortSpy (ComponentType *component) : component (component) {}

  const std::vector<tl::optional<Message<PortType>>> &
  getRecordedMessages () const
  {
    return recordedMessages;
  }
};


<#list mainComp.getPorts() as port>
  <#assign compTypeName = mainComp.getName()>
  <#assign portName = port.getName()>
  <@portSpy.printPortSpy compTypeName=compTypeName portName=portName package=package port=port/>
</#list>


<#list mainComp.getSubComponents() as component>
  <#list component.getType().getPorts() as port>
    <#assign compTypeName = component.getType().getName()>
    <#assign compName = component.getName()>
    <#assign portName = port.getName()>
    <@portSpy.printPortSpy compTypeName=compTypeName portName=portName package=package port=port compName=compName isComponent=true/>

  </#list>


</#list>


// Check that is correctly connected to Sink (i.e. Sink receives Source's messages)
TEST_F (${mainComp.getName()}Test, ${ast.getTestDiagram().getName()})
{
  // Given
  <#assign compTypeName = mainComp.getName()>
  // PortSpy of the "${compTypeName}" component
  <#list mainComp.getPorts() as port>
    <#assign portName = port.getName()?cap_first>
  LOG(INFO) << "PortSpy to port ${portName} of main component cmp${mainCompName} attached";
  PortSpy_${compTypeName}_${portName} portSpy${compTypeName}${portName}(cmp${mainCompName});
  cmp${mainCompName}->getInterface()->getPort${portName}()->attach(&portSpy${compTypeName}${portName});

  </#list>

<#list mainComp.getSubComponents() as component>
  <#assign compName = component.getName()>
  <#assign compTypeName = component.getType().getName()>
  // PortSpy of the "${compName}" component
  <#list component.getType().getPorts() as port>
    <#assign portName = port.getName()?cap_first>
  LOG(INFO) << "PortSpy to port ${portName} of sub component ${compName}Cmp attached";
  PortSpy_${compTypeName}_${compName?cap_first}_${portName} portSpy${compTypeName}${compName?cap_first}${portName}(${compName}Cmp);
  ${compName}Cmp->getInterface()->getPort${portName}()->attach(&portSpy${compTypeName}${compName?cap_first}${portName});

  </#list>

</#list>

  // When
  cmp${mainCompName}->setUp(EVENTBASED);
  cmp${mainCompName}->init();
  cmp${mainCompName}->start();


  // tests:
<#assign testDiagramSymbol = ast.getEnclosingScope().getDiagramSymbols().values()[0]>
<#assign testDiagramComp = testDiagramSymbol.getAstNode()>
<#if testDiagramComp.getSD4CElementList()[0].getType() != "MAIN_INPUT">
  LOG(INFO) << "start computing";
  cmp${mainCompName}->compute();
</#if>

<#assign refCounterList = {}>
<#list testDiagramComp.getSD4CElementList() as sD4CElement>
  <#if sD4CElement.getType() == "MAIN_INPUT">
  // Input von mainComp setzen
    <#assign portName = sD4CElement.getTarget(0).getPort()?cap_first>
    <#assign portType = mainComp.getPort(sD4CElement.getTarget(0).getPort()).get().getType().getTypeInfo().getName()>
  LOG(INFO) << "start computing with next value ${sD4CElement.getValue(0).getValue()}";
  cmp${mainCompName}->getInterface()->getPort${portName}()->setNextValue(Message<${portType}>(${sD4CElement.getValue(0).getValue()}));

  <#elseif sD4CElement.getType() == "MAIN_OUTPUT">
  // Output von mainComp prüfen
    <#assign compTypeName = mainComp.getName()>
    <#assign portName = sD4CElement.getSource().getPort()?cap_first>
  LOG(INFO) << "check main output";
  ASSERT_TRUE (portSpy${compTypeName}${portName}.getRecordedMessages().back().has_value());
  EXPECT_EQ (portSpy${compTypeName}${portName}.getRecordedMessages().back().value().getPayload(), ${sD4CElement.getValue(0).getValue()});

  <#elseif sD4CElement.getType() == "DEFAULT">
    <#list sD4CElement.getTargetList() as portAccess>
      <#if portAccess.isPresentComponent()>
  // Input von Target ${portAccess.getComponent()}.${portAccess.getPort()} prüfen
        <#assign compName = portAccess.getComponent()>
        <#assign compTypeName = mainComp.getSubComponent(portAccess.getComponent()).get().getType().getName()>
      <#else>
  // Input von Target ${portAccess.getPort()} prüfen
        <#assign compName = "">
        <#assign compTypeName = mainComp.getName()>
      </#if>
      <#assign portName = portAccess.getPort()?cap_first>
      <#if !refCounterList["portSpy" + compTypeName + compName + portName]?? >
        <#assign refCounterList = refCounterList + {"portSpy" + compTypeName + compName + portName : 0}>
      <#else >
        <#assign refCounterList = refCounterList + {"portSpy" + compTypeName + compName + portName : (refCounterList["portSpy" + compTypeName + compName + portName] + 1)}>
      </#if>
  LOG(INFO) << "check ${prettyPrinter.prettyprint(sD4CElement)?replace("\n", "")?replace("\r", "")}";
  ASSERT_TRUE (portSpy${compTypeName}${compName?cap_first}${portName}.getRecordedMessages().at(${refCounterList["portSpy" + compTypeName + compName + portName]}).has_value());
      <#if sD4CElement.getValueList()?size < 2 >
  EXPECT_EQ (portSpy${compTypeName}${compName?cap_first}${portName}.getRecordedMessages().at(${refCounterList["portSpy" + compTypeName + compName + portName]}).value().getPayload(), ${sD4CElement.getValue(0).getValue()});
      <#else>
  EXPECT_EQ (portSpy${compTypeName}${compName?cap_first}${portName}.getRecordedMessages().at(${refCounterList["portSpy" + compTypeName + compName + portName]}).value().getPayload(), ${sD4CElement.getValue(portAccess?index).getValue()});
      </#if>

    </#list>
  <#elseif sD4CElement.getType() == "EXPRESSION">
  // test Expression
  LOG(INFO) << "check expression ${prettyPrinter.prettyprint(sD4CElement)?replace("\n", "")?replace("\r", "")}";
  ASSERT_TRUE(${cppPrettyPrinter.prettyprint(sD4CElement)});

  </#if>
</#list>
}
