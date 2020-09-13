#pragma once
#include "SinkInput.h"
#include "SinkResult.h"
#include "IComputable.h"
#include <stdexcept>

namespace montithings {
namespace ad_test_decision {

class SinkImpl : IComputable<SinkInput,SinkResult>{
	
private:  
    
public:
    SinkImpl()
    {
    }
	//SinkImpl() = default;
	SinkResult getInitialValues() override;
	SinkResult compute(SinkInput input) override;
};

}}