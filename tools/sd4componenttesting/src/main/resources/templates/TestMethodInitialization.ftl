<#-- (c) https://github.com/MontiCore/monticore -->
<#macro printTestMethodInitialization ast mainComp mainCompName>
  <#assign compTypeName = mainCompName>
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


  <#assign testDiagramSymbol = ast.getEnclosingScope().getDiagramSymbols().values()[0]>
  <#assign testDiagramComp = testDiagramSymbol.getAstNode()>
  <#if testDiagramComp.getSD4CElementList()[0]??>
  <#if testDiagramComp.getSD4CElementList()[0].getType() != "MAIN_INPUT">
  LOG(INFO) << "start computing";
  cmp${mainCompName}->compute();
  </#if>
  </#if>
</#macro>
