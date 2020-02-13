#pragma once
#include "LowPassFilterInput.h"
#include "LowPassFilterResult.h"
#include "IComputable.h"
#include <stdexcept>


class LowPassFilterImpl : IComputable<LowPassFilterInput,LowPassFilterResult>{
	
private:  
    int threshold;
    
public:
    LowPassFilterImpl( int threshold )
    :
    threshold (threshold)
    {
    }
	//LowPassFilterImpl() = default;
	LowPassFilterResult getInitialValues() override;
	LowPassFilterResult passthrough(LowPassFilterInput input);
	LowPassFilterResult dismiss(LowPassFilterInput input);
	LowPassFilterResult compute(LowPassFilterInput input) override {
	  throw std::runtime_error("Invoking compute() on component hierarchy.LowPassFilter which has if-then-else behavior");
	}
};

