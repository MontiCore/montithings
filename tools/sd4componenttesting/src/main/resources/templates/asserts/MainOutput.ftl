<#-- (c) https://github.com/MontiCore/monticore -->
<#macro printMainOutput sD4CElement mainCompName>
  // check output of mainComp
  <#assign compTypeName = mainCompName>
  <#assign portName = sD4CElement.getSource().getPort()?cap_first>
  LOG(INFO) << "check main output";
  ASSERT_TRUE (portSpy${compTypeName}${portName}.getRecordedMessages().back().has_value());
  EXPECT_EQ (portSpy${compTypeName}${portName}.getRecordedMessages().back().value().getPayload(), ${sD4CElement.getValue(0).getValue()});

</#macro>
