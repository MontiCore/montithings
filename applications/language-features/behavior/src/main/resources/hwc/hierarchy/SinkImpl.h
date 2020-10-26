// (c) https://github.com/MontiCore/monticore
#pragma once
#include <SinkImplTOP.h>

namespace montithings {
namespace hierarchy {

class SinkImpl : SinkImplTOP {
	
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