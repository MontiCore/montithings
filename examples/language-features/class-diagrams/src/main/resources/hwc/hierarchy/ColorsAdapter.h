// (c) https://github.com/MontiCore/monticore
#pragma once
#include "ColorsAdapterTOP.h"

namespace montithings
{
namespace hierarchy
{

class ColorsAdapter : public ColorsAdapterTOP
{
  private:
  public:
  ColorsAdapter () = default;
  Colors::Color convertColor (uint8_t element) override;
  uint8_t convertColor (Colors::Color element) override;
};

} // namespace hierarchy
} // namespace montithings
