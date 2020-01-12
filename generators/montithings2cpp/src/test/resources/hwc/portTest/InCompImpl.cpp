// (c) https://github.com/MontiCore/monticore
#include "InCompImpl.h"
#include <iostream>

InCompResult InCompImpl::getInitialValues(){
	return InCompResult();
	
}

InCompResult InCompImpl::compute(InCompInput input){
    auto stringOpt = input.getInPort();

    if (stringOpt){
        std::cout << stringOpt.value() << "\n";
    } else{
        std::cout << "No value present\n";
    }
        return InCompResult();
  	  	
  	  }
