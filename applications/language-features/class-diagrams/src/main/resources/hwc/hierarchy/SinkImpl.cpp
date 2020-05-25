#include "SinkImpl.h"
#include <iostream>

namespace montithings {
namespace hierarchy {

SinkResult
SinkImpl::getInitialValues ()
{
  return {};
}

SinkResult
SinkImpl::compute (SinkInput input)
{
  if (input.getValue ())
      {
        std::string color;
        switch (input.getValue ().value ()) {
          case Colors::Red:
            color = "Red";
            break;
          case Colors::Green:
            color = "Green";
            break;
          case Colors::Blue:
            color = "Blue";
            break;
          default:
            color = "No data.";
        }
        std::cout << color << std::endl;
      }
    else
      { std::cout << "No data." << std::endl; }
    return {};
}

}}