// (c) https://github.com/MontiCore/monticore
#include <iostream>
#include "SourceImpl.h"

namespace montithings {
namespace hierarchy {

SourceResult SourceImpl::getInitialValues(){
	return {};
}

SourceResult SourceImpl::compute(SourceInput input){
  std::cout << "Source: " << lastValue << std::endl;
	return {lastValue++};
}

}}