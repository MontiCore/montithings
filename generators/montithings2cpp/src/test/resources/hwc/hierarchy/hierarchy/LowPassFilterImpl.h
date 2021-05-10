// (c) https://github.com/MontiCore/monticore
#pragma once
#include "LowPassFilterImplTOP.h"

namespace montithings {
namespace hierarchy {

class LowPassFilterImpl : public LowPassFilterImplTOP {
	
private:  
    int threshold;
    
public:
  using LowPassFilterImplTOP::LowPassFilterImplTOP;
	LowPassFilterResult getInitialValues() override;
	LowPassFilterResult passthrough(LowPassFilterInput input);
	LowPassFilterResult dismiss(LowPassFilterInput input);
};

}}
