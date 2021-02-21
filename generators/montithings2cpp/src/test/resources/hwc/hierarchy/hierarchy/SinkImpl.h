// (c) https://github.com/MontiCore/monticore
#pragma once
#include "SinkInput.h"
#include "SinkResult.h"
#include "IComputable.h"
#include <stdexcept>

namespace montithings {
namespace hierarchy {

class SinkImpl : IComputable<SinkInput,SinkResult>{
	
private:  
    
public:
  using SinkImplTOP::SinkImplTOP;
	SinkResult getInitialValues() override;
	SinkResult compute(SinkInput input) override;
};

}}