// (c) https://github.com/MontiCore/monticore
#pragma once
#include "SinkImplTOP.h"

namespace montithings
{
namespace hierarchy
{

class SinkImpl : public SinkImplTOP
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