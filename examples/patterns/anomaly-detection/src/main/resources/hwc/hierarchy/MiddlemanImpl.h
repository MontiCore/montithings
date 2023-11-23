// (c) https://github.com/MontiCore/monticore
#pragma once
#include "MiddlemanImplTOP.h"

namespace montithings
{
  namespace hierarchy
  {

    class MiddlemanImpl : public MiddlemanImplTOP
    {

    public:
      using MiddlemanImplTOP::MiddlemanImplTOP;
      MiddlemanResult getInitialValues() override;
      MiddlemanResult compute(MiddlemanInput input) override;
    };

  } // namespace hierarchy
} // namespace montithings
