#pragma once
#include <SumImplTOP.h>

namespace montithings {
namespace hierarchy {

class SumImpl : public SumImplTOP
{
  public:
  SumImpl () = default;
  SumResult getInitialValues () override;
  SumResult compute (SumInput input) override;
};

}}