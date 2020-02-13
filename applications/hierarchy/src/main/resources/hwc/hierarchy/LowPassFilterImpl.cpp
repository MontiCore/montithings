#include "LowPassFilterImpl.h"

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
