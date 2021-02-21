// (c) https://github.com/MontiCore/monticore
#pragma once
#include "SourceImplTOP.h"

namespace montithings {
namespace hierarchy {

class SourceImpl : public SinkImplTOP {
	
private:  
    
public:
  int lastValue;
  using SinkImplTOP::SinkImplTOP;
	SourceResult getInitialValues() override;
	SourceResult compute(SourceInput input) override;
};

}}