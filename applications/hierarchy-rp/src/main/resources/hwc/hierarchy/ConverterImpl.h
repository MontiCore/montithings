#pragma once
#include "ConverterInput.h"
#include "ConverterResult.h"
#include "IComputable.h"
#include <stdexcept>

namespace montithings {
namespace hierarchy {

class ConverterImpl : IComputable<ConverterInput,ConverterResult>{
	
private:  
    
public:
    ConverterImpl()
    {
    }
	//ConverterImpl() = default;
	ConverterResult getInitialValues() override;
	ConverterResult setZero(ConverterInput input);
	ConverterResult propagate(ConverterInput input);
	ConverterResult compute(ConverterInput input) override {
	  throw std::runtime_error("Invoking compute() on component hierarchy.Converter which has if-then-else behavior");
	}
};

}}