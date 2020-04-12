// (c) https://github.com/MontiCore/monticore
#pragma once
#include "UniqueElement.h"

enum TimeMode
{
  TIMESYNC,
  EVENTBASED
};

class IComponent : public UniqueElement
{
  virtual void setUp (TimeMode enclosingComponentTiming) = 0;
  virtual void init () = 0;
  virtual void compute () = 0;
  virtual void start () = 0;
};

