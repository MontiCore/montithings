#include "SubCompImpl.h"

SubCompResult SubCompImpl::getInitialValues(){
	return SubCompResult();
	
}

SubCompResult SubCompImpl::compute(SubCompInput input){
    SubCompResult result;
    if (input.getInPort()) {
        result.setOutPort(input.getInPort().value());
    }
    return result;
}