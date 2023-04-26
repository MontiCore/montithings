// (c) https://github.com/MontiCore/monticore
#include "WindowSensorCombinerImpl.h"
#include <iostream>

namespace montithings {
namespace smartHeat {

WindowSensorCombinerResult
WindowSensorCombinerImpl::getInitialValues ()
{
  return {};
}

WindowSensorCombinerResult
WindowSensorCombinerImpl::compute (WindowSensorCombinerInput input)
{
  if (input.getWindow_sensor_state().has_value())
  {
    auto msg = input.getWindow_sensor_state().value();
    if(msg.getState() == false){
      ids.insert(msg.getSensor_id());
    }else{
      ids.erase(msg.getSensor_id());
    }
    WindowSensorCombinerResult result;
    WindowSensorCombinerMessages::WindowRoomState res = new WindowSensorCombinerMessages::WindowRoomState();
    if(ids.size() > 0){
      res.setState(false);
      result.setWindow_room_state(res);
    }else{
      res.setState(true);
      result.setWindow_room_state(res);
    }
    interface.getPortWindow_room_state()->setNextValue(result.getWindow_room_stateMessage());
    return result;
  }
  return {};
}

}}