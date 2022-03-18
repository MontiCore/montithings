<#-- (c) https://github.com/MontiCore/monticore -->
<#macro printMainInput sD4CElement mainComp mainCompName>
  // set input of mainComp
  <#assign portName = sD4CElement.getTarget(0).getPort()?cap_first>
  <#assign portType = mainComp.getPort(sD4CElement.getTarget(0).getPort()).get().getType().getTypeInfo().getName()>
  LOG(INFO) << "start computing with next value ${sD4CElement.getValue(0).getValue()}";
  cmp${mainCompName}->getInterface()->getPort${portName}()->setNextValue(Message<${portType}>(${sD4CElement.getValue(0).getValue()}));

</#macro>
