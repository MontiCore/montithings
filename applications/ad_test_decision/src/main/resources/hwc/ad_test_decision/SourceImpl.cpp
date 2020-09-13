#include <iostream>
#include "SourceImpl.h"

namespace montithings {
namespace ad_test_decision {

SourceResult SourceImpl::getInitialValues(){
    lastValue = 1;
	return {lastValue};
}

SourceResult SourceImpl::compute(SourceInput input){
	return {lastValue++};
}

}}