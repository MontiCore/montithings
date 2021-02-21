// (c) https://github.com/MontiCore/monticore
#pragma once
#include "LowPassFilterImplTOP.h"

namespace montithings {
namespace hierarchy {

class LowPassFilterImpl : LowPassFilterImplTOP {
	
private:  
    int threshold;
    
public:
  using LowPassFilterImplTOP::LowPassFilterImplTOP;
	LowPassFilterResult getInitialValues() override;
	LowPassFilterResult passthrough(LowPassFilterInput input);
	LowPassFilterResult dismiss(LowPassFilterInput input);
	LowPassFilterResult compute(LowPassFilterInput input) override {
	  throw std::runtime_error("Invoking compute() on component hierarchy.LowPassFilter which has if-then-else behavior");
	}
};

}}