#include <iostream>
#include "SubCompImpl.h"

SubCompResult SubCompImpl::getInitialValues(){
	return SubCompResult();
	
}

SubCompResult SubCompImpl::compute(SubCompInput input){
    SubCompResult result;
    if (input.getInPort()) {
        auto arr = input.getInPort().value();
        std::cout << arr[0];
        result.setOutPort(arr[0]);

    }
    return result;
}