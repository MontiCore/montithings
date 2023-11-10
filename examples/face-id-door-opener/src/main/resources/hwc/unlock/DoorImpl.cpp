// (c) https://github.com/MontiCore/monticore
#include "DoorImpl.h"
#include <iostream>

namespace montithings {
namespace unlock {

DoorResult
DoorImpl::getInitialValues ()
{
  return {};
}

DoorResult
DoorImpl::compute (DoorInput input)
{
  if (input.getVisitor().has_value())
    {
      auto visitor = input.getVisitor().value();
      if (visitor.getAllowed())
        {
          std::cout << "[Door-C++] Access granted to: " << visitor.getName() << std::endl;
        } else {
          std::cout << "[Door-C++] Access denied to: " << visitor.getName() << std::endl;
        }
    }
  else
    {
      std::cout << "[Door-C++] No data." << std::endl;
    }

  return {};
}

}}