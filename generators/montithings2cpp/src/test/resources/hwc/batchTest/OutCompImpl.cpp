#include "OutCompImpl.h"

OutCompResult OutCompImpl::getInitialValues(){
	OutCompResult result;
	result.setOutPort("Initial Value");
	return result;
	
}


OutCompResult OutCompImpl::compute(OutCompInput input){
    OutCompResult result;
    result.setOutPort("Value");
    return result;
}
