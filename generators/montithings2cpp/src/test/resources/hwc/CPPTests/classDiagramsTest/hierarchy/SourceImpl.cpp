// (c) https://github.com/MontiCore/monticore
#include <iostream>
#include "SourceImpl.h"

namespace montithings
{
namespace hierarchy
{

SourceResult
SourceImpl::getInitialValues ()
{
  return {};
}

SourceResult
SourceImpl::compute (SourceInput input)
{
  uint8_t vector = {static_cast<uint8_t>(rand () % 4)};
  SourceResult result;
  result.setValue (vector);
  return result;
}

}
}