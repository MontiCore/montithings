// (c) https://github.com/MontiCore/monticore
#pragma once

enum TimeMode
{
  TIMESYNC,
  EVENTBASED
};

class IComponent
{
  virtual void setUp (TimeMode enclosingComponentTiming) = 0;
  virtual void init () = 0;
  virtual void compute () = 0;
  virtual void start () = 0;
};

