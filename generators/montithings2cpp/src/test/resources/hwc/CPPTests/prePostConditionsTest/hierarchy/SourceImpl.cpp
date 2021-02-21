// (c) https://github.com/MontiCore/monticore
#include <iostream>
#include "SourceImpl.h"

namespace montithings {
namespace hierarchy {

SourceResult SourceImpl::getInitialValues(){
  state.setLastValue(1);
  return {state.getLastValue()};
}

SourceResult SourceImpl::compute(SourceInput input){
  std::cout << "Source: " << state.getLastValue() << std::endl;
  state.setLastValue(state.getLastValue() + 1);
  return {state.getLastValue()};
}

}}