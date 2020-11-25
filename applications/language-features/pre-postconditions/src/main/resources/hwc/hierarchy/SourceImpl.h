// (c) https://github.com/MontiCore/monticore
#pragma once
#include "SourceImplTOP.h"

namespace montithings {
namespace hierarchy {

class SourceImpl : public SourceImplTOP {
	
private:  
    
public:
    SourceImpl()
    {
    }

    int lastValue;

	//SourceImpl() = default;
	SourceResult getInitialValues() override;
	SourceResult compute(SourceInput input) override;
};

}}