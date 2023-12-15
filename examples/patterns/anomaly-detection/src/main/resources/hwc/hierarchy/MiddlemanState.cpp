// (c) https://github.com/MontiCore/monticore
#include "MiddlemanState.h"

namespace montithings
{
  namespace hierarchy
  {
    int
    MiddlemanState::getRunningIndex()
    {
      return this->runningIndex;
    }

    void
    MiddlemanState::postSetRunningIndex(int idx)
    {
      this->runningIndex = idx;
    }
  } // namespace hierarchy
} // namespace montithings