// (c) https://github.com/MontiCore/monticore
#include "ConverterImpl.h"

namespace montithings {
namespace hierarchy {

ConverterResult ConverterImpl::getInitialValues(){
	return {0};
}

ConverterResult ConverterImpl::compute(ConverterInput input){
  if (!input.getInport()) : return {0};
	return {input.getInport().value()};
}

}}