#pragma once
#include "SourceInput.h"
#include "SourceResult.h"
#include "SourceImplTOP.h"
#include "IComputable.h"
#include <stdexcept>
#include "Colors/Color.h"

namespace montithings {
namespace hierarchy {

class SourceImpl : SourceImplTOP {
	
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