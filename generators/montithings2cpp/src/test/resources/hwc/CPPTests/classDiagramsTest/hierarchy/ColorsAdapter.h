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
  Colors::Color convert (uint8_t element) override;
  uint8_t convert (Colors::Color element) override;
};

} // namespace hierarchy
} // namespace montithings
