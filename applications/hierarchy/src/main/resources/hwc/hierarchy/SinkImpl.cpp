// (c) https://github.com/MontiCore/monticore
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
      log("Sink | in: " +  input.getValue ().value ());
    }
  else
    { std::cout << "No data." << std::endl; }
  return {};
}

}}