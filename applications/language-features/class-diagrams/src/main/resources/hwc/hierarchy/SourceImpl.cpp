#include <iostream>
#include "SourceImpl.h"

namespace montithings {
namespace hierarchy {

SourceResult SourceImpl::getInitialValues(){
	return {};
}

SourceResult SourceImpl::compute(SourceInput input){
	arma::vec vector = { static_cast<double>(rand() % 3) };
	SourceResult result;
	result.setValue(vector);
	return result;
}

}}