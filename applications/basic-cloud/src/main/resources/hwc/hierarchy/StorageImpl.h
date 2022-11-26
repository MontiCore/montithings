// (c) https://github.com/MontiCore/monticore
#pragma once
#include "StorageImplTOP.h"

namespace montithings {
namespace hierarchy {

class StorageImpl : public StorageImplTOP {
	
private:  
    
public:
  using StorageImplTOP::StorageImplTOP;
	StorageResult getInitialValues() override;
	StorageResult compute(StorageInput input) override;
};

}}