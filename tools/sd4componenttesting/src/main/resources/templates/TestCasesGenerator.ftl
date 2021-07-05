// (c) https://github.com/MontiCore/monticore
#include "easyloggingpp/easylogging++.h"
#include "gtest/gtest.h"
#include <chrono>
#include <thread>

<#assign mainComp = ast.getEnclosingScope().getMainComponentTypeSymbol()>
<#assign mainCompName = mainComp.getName()>
<#-- TODO package default ?!?! -->
<#assign package = "montithings">
<#list ast.getPackageDeclaration().getPartsList() as name>
  <#assign package = package + "::" + name>
</#list>
<#assign package = package + "::" >

#include "${mainComp.getName()}.h"

<#-- TODO: bei mehreren subComponents des gleichen Typs wird mehrmals die gleiche Header Datei eingebunden -->
<#list mainComp.getSubComponents() as component>
#include "${component.getType().getName()}.h"
</#list>

INITIALIZE_EASYLOGGINGPP

struct ${ast.getTestDiagram().getName()} : testing::Test
{
  ${package}${mainComp.getName()} *cmp${mainCompName};

<#list mainComp.getSubComponents() as component>
  ${package}${component.getType().getName()} *${component.getName()}Cmp;
  ${package}${component.getType().getName()}Impl *${component.getName()}Impl;
  ${package}${component.getType().getName()}State *${component.getName()}State;

</#list>

  ${ast.getTestDiagram().getName()} ()
  {
    <#--  //TODO wofür steht das "example" -->
    cmp${mainCompName} = new ${package}${mainComp.getName()} ("example");

<#list mainComp.getSubComponents() as component>
    ${component.getName()}Cmp = cmp${mainCompName}->getSubcomp__${component.getType().getName()}();
    ${component.getName()}Impl = ${component.getName()}Cmp->getImpl();
    ${component.getName()}State = ${component.getName()}Cmp->getState();

</#list>
  }

  ~${ast.getTestDiagram().getName()} ()
  {
    delete cmp;
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
  std::vector<tl::optional<PortType>> recordedMessages;

public:
  explicit PortSpy (ComponentType *component) : component (component) {}

  const std::vector<tl::optional<PortType>> &
  getRecordedMessages () const
  {
    return recordedMessages;
  }
};


<#list mainComp.getSubComponents() as component>
  <#list component.getType().getPorts() as port>
    <#assign compTypeName = component.getType().getName()>
    <#assign compName = component.getName()>
    <#assign portName = port.getName()>
/**
 * This class records values of the "${compName}" component's "${portName}" port
 */
class PortSpy_${compTypeName}_${compName?cap_first}_${portName?cap_first} : public PortSpy<${package}${compTypeName}, ${port.getType().getTypeInfo().getName()}>
{
public:
  using PortSpy::PortSpy;

  void onEvent () override
  {
    tl::optional<${port.getType().getTypeInfo().getName()}> value
        = component->getInterface ()->getPort${portName?cap_first} ()->getCurrentValue (this->getUuid ());
    recordedMessages.push_back (value);
  }
};

  </#list>

</#list>


// Check that is correctly connected to Sink (i.e. Sink receives Source's messages)
TEST_F (${ast.getTestDiagram().getName()}, Wiring)
{
  // Given
<#list mainComp.getSubComponents() as component>
  // PortSpy of the "${compName}" component
  <#list component.getType().getPorts() as port>
    <#assign compTypeName = component.getType().getName()>
    <#assign compName = component.getName()>
    <#assign portName = port.getName()?cap_first>
  PortSpy_${compTypeName}_${compName?cap_first}_${portName} portSpy${compTypeName}${compName?cap_first}${portName}(${compName}Cmp);
  ${compName}Cmp->getInterface()->getPort${portName}()->attach(&portSpy${compTypeName}${compName?cap_first}${portName});

  </#list>

</#list>

  // When
  cmp${mainCompName}->setUp(EVENTBASED);
  cmp${mainCompName}->init();

<#--
//Ablauf der Tests:
<#if mainComp.getConnections().get(0).getType() != "MAIN_INPUT">
  mainComp.compute();
</#if>
-->

<#--  TODO wir müssen die Connections bekommen -->

<#--
<#list mainComp.getConnections() as connection>
  <#if connection.getType() == "MAIN_INPUT">
  // Input von mainComp setzen (mainComp.getInterface().getPortFirst()->setNextValue())
  // mainComp.compute() aufrufen (nur wenn nächste Connection != MAIN_INPUT)
  <#elseif connection.getType() == "MAIN_OUTPUT">
  // Output von mainComp prüfen
  <#else>
  // Targets Ports haben den gegeben Wert (am richtigen Zeitpunkt)
  </#if>
</#list>
-->
  for (int i = 0; i < 33; i++)
  {
    source->compute__Every1 ();
  }

  // Then
  for (int i = 0; i < 2; i++)
  {
    ASSERT_TRUE (portSpyExampleSourceValue.getRecordedMessages ().at (i).has_value ());
    EXPECT_EQ (portSpyExampleSourceValue.getRecordedMessages ().at (i).value (), i);
    ASSERT_TRUE (portSpySinkValue.getRecordedMessages ().at (i).has_value ());
    EXPECT_EQ (portSpySinkValue.getRecordedMessages ().at (i).value (), i);
  }
}
