// (c) https://github.com/MontiCore/monticore
#pragma once
#include <SumImplTOP.h>

namespace montithings {
namespace hierarchy {

class SumImpl : public SumImplTOP
{
  public:
  using SumImplTOP::SumImplTOP;
  SumResult getInitialValues () override;
  SumResult compute (SumInput input) override;
};

}}