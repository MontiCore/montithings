<#-- (c) https://github.com/MontiCore/monticore -->
if (nextVal)
  {
    std::cout << "Sink: " << nextVal.value () << std::endl;
  }
else
  { 
  	std::cout << "Sink: " << "${glex.getGlobalVar("globalVarConsume").getExpression(0).getLiteral().getValue()!}" << std::endl;
  }