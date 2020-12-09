if (nextVal)
  {
    std::cout << "Sink: " << nextVal.value () << std::endl;
  }
else
  { 
  	std::cout << "Sink: " << "${glex.getGlobalVar("globalVarSendToExternal").getExpression(0).getLiteral().getValue()!}" << std::endl;
  }