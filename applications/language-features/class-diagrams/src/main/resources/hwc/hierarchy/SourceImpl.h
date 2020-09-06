#pragma once
#include "SourceImplTOP.h"

namespace montithings
{
namespace hierarchy
{

class SourceImpl : SourceImplTOP
{

  private:

  public:
  SourceImpl ()
  {
  }

  int lastValue;
  SourceResult getInitialValues () override;
  SourceResult compute (SourceInput input) override;
};

}
}