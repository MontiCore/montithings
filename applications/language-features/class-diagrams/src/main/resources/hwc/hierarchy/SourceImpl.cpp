#include <iostream>
#include "SourceImpl.h"

namespace montithings {
namespace hierarchy {

SourceResult SourceImpl::getInitialValues(){
	return {};
}

SourceResult SourceImpl::compute(SourceInput input){
	return {static_cast<Colors::Color>(rand() % 3)};
}

}}