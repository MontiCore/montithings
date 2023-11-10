// (c) https://github.com/MontiCore/monticore
#pragma once
#include "WindowSensorCombinerImplTOP.h"
#include <set>
#include "../WindowSensorCombinerMessages/WindowRoomState.h"

namespace montithings {
namespace smartHeat {

class WindowSensorCombinerImpl : public WindowSensorCombinerImplTOP {
	
private: 
	std::set<int> ids;
    
public:
  using WindowSensorCombinerImplTOP::WindowSensorCombinerImplTOP;
	WindowSensorCombinerResult getInitialValues() override;
	WindowSensorCombinerResult compute(WindowSensorCombinerInput input) override;
};

}}