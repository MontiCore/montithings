#pragma once
#include "SourceInput.h"
#include "SourceResult.h"
#include "IComputable.h"
#include <stdexcept>

namespace montithings {
namespace ad_test_decision {

class SourceImpl : IComputable<SourceInput,SourceResult>{
	
private:  
    
public:
    SourceImpl()
    {
    }

    int lastValue;

	SourceResult getInitialValues() override;
	SourceResult compute(SourceInput input) override;
};

}}