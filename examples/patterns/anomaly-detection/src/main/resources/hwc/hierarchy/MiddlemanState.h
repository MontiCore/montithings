// (c) https://github.com/MontiCore/monticore
#pragma once
#include "MiddlemanStateTOP.h"

namespace montithings
{
  namespace hierarchy
  {
    class MiddlemanState : public MiddlemanStateTOP
    {

    protected:
      int runningIndex = 0;

    public:
      using MiddlemanStateTOP::MiddlemanStateTOP;
      int getRunningIndex();
      void postSetRunningIndex(int idx);
    };
  } // namespace hierarchy
} // namespace montithings