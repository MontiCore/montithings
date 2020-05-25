#pragma once
#include "SourceInput.h"
#include "SourceResult.h"
#include "IComputable.h"
#include <stdexcept>

namespace montithings {
namespace hierarchy {

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