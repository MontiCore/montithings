#pragma once
#include "SinkInput.h"
#include "SinkResult.h"
#include "IComputable.h"
#include <stdexcept>


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

