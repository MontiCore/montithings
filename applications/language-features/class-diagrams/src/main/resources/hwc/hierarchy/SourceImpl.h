// (c) https://github.com/MontiCore/monticore
#pragma once
#include "SourceImplTOP.h"

namespace montithings
{
namespace hierarchy
{

class SourceImpl : public SourceImplTOP
{

  private:

  public:
  using SourceImplTOP::SourceImplTOP;
  int lastValue = 0;
  SourceResult getInitialValues () override;
  SourceResult compute (SourceInput input) override;
};

}
}