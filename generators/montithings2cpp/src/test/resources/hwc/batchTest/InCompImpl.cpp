// (c) https://github.com/MontiCore/monticore
#include "InCompImpl.h"
#include <iostream>

InCompResult InCompImpl::getInitialValues(){
	return InCompResult();
	
}

InCompResult InCompImpl::compute(InCompInput input){
    auto stringOpt = input.getInPort();


        std::cout << "Got " << stringOpt.size() << " inputs \n";


        return InCompResult();
  	  	
  	  }
