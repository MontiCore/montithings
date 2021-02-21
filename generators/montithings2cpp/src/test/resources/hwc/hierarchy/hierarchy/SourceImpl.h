// (c) https://github.com/MontiCore/monticore
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
  int lastValue;
  using SourceImplTOP::SourceImplTOP;
	SourceResult getInitialValues() override;
	SourceResult compute(SourceInput input) override;
};

}}