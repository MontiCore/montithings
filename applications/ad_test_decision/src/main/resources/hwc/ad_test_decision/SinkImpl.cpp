#include "SinkImpl.h"
#include <iostream>

namespace montithings {
namespace ad_test_decision {

SinkResult
SinkImpl::getInitialValues ()
{
  return {};
}

SinkResult
SinkImpl::compute (SinkInput input)
{
  if (input.getInport ())
  {
     std::cout << input.getInport ().value() << std::endl;
  }
  else
    { std::cout << "No data." << std::endl; }
  return {};
}

}}