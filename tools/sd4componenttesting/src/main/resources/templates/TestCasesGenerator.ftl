// (c) https://github.com/MontiCore/monticore
#include "easyloggingpp/easylogging++.h"
#include "gtest/gtest.h"
#include <chrono>
#include <thread>

<#assign mainComp = ast.getEnclosingScope().getMainComponentTypeSymbol()>
<#assign mainCompName = mainComp.getName()>
//TODO package default ?!?!
<#assign package = "montithings">
<#list ast.getPackageDeclaration().getPartsList() as name>
  <#assign package = package + "::" + name>
</#list>
<#assign package = package + "::" >

#include "${mainComp.getName()}.h"

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
    //TODO wofür steht das "example"
    cmp = new ${package}${mainComp.getName()} ("example");

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
