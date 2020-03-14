#pragma once
#include "SumInput.h"
#include "SumResult.h"
#include "IComputable.h"
#include <stdexcept>

namespace montithings {
namespace hierarchy {

class SumImpl : IComputable<SumInput,SumResult>{
	
private:  
    
public:
    SumImpl()
    {
    }
	//SumImpl() = default;
	SumResult getInitialValues() override;
	SumResult compute(SumInput input) override;
};

}}