// (c) https://github.com/MontiCore/monticore
#include "LowPassFilterImpl.h"

namespace montithings {
namespace hierarchy {

LowPassFilterResult
LowPassFilterImpl::getInitialValues ()
{
  return {};
}

LowPassFilterResult
LowPassFilterImpl::passthrough (LowPassFilterInput input)
{
  return {input.getInport ().value ()};
}

LowPassFilterResult
LowPassFilterImpl::dismiss (LowPassFilterInput input)
{
  return {};
}

}}