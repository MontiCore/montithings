#include "InCompImpl.h"
#include <iostream>

InCompResult InCompImpl::getInitialValues(){
	return InCompResult();
	
}

InCompResult InCompImpl::compute(InCompInput input){
    auto stringOpt = input.getInPort();


        std::cout << stringOpt.size() << "\n";

        std::cout << "First Compute Done!\n";

        return InCompResult();
  	  	
  	  }
