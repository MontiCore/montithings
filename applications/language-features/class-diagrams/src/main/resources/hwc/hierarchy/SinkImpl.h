#pragma once
#include "SinkImplTOP.h"

namespace montithings
{
namespace hierarchy
{

class SinkImpl : SinkImplTOP
{

  private:

  public:
  SinkImpl ()
  {
  }

  SinkResult getInitialValues () override;
  SinkResult compute (SinkInput input) override;
};

}
}