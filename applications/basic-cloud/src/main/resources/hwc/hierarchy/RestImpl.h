// (c) https://github.com/MontiCore/monticore
#pragma once
#include "RestImplTOP.h"

namespace montithings {
namespace hierarchy {

class RestImpl : public RestImplTOP {
	
private:  
    
public:
  using RestImplTOP::RestImplTOP;
	RestResult getInitialValues() override;
	RestResult compute(RestInput input) override;
};

}}