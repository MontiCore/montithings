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
      std::cout << "Sink: " << input.getValue ().value () << std::endl;
    }
  else
    { std::cout << "Sink: " << "No data." << std::endl; }
  return {};
}

}}