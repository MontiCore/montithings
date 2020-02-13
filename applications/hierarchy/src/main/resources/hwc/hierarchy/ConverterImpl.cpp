#include "ConverterImpl.h"
ConverterResult ConverterImpl::getInitialValues(){
	return {0};
}

ConverterResult ConverterImpl::setZero(ConverterInput input){
	return {0};
}

ConverterResult ConverterImpl::propagate(ConverterInput input){
	return {input.getInport().value()};
}
