#include <iostream>
#include "SumImpl.h"

SumResult
SumImpl::getInitialValues ()
{
  return {};
}

SumResult
SumImpl::compute (SumInput input)
{
  if (!input.getIn1 () || !input.getIn2 ())
    { return {}; }
  return {input.getIn1 ().value () + input.getIn2 ().value ()};
}
