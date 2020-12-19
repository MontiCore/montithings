if (nextVal)
  {
    std::cout << "Sink: " << nextVal.value () << std::endl;
  }
else
  { 
  	std::cout << "Sink: " << "${glex.getGlobalVar("globalVarconsume").getExpression(0).getLiteral().getValue()!}" << std::endl;
  }