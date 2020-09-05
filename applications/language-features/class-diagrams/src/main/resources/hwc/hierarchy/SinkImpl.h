#pragma once
#include "SinkInput.h"
#include "SinkResult.h"
#include "SinkImplTOP.h"
#include "IComputable.h"
#include <stdexcept>
#include "Colors/Color.h"

namespace montithings {
namespace hierarchy {

class SinkImpl : SinkImplTOP {
	
private:  
    
public:
    SinkImpl()
    {
    }
	SinkResult getInitialValues() override;
	SinkResult compute(SinkInput input) override;
};

}}