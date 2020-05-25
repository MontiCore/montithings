#pragma once
#include "LowPassFilterInput.h"
#include "LowPassFilterResult.h"
#include "IComputable.h"
#include <stdexcept>


namespace montithings {
namespace hierarchy {

class LowPassFilterImpl : IComputable<LowPassFilterInput,LowPassFilterResult>{
	
private:  
    int threshold;int defaultValue;
    
public:
    LowPassFilterImpl( int threshold,  int defaultValue )
    :
    threshold (threshold),
    defaultValue (defaultValue)
    {
    }
	//LowPassFilterImpl() = default;
	LowPassFilterResult getInitialValues() override;
	LowPassFilterResult compute(LowPassFilterInput input) override {
	  throw std::runtime_error("Invoking compute() on component hierarchy.LowPassFilter which has if-then-else behavior");
	}
};

} // namespace hierarchy
} // namespace montithings
