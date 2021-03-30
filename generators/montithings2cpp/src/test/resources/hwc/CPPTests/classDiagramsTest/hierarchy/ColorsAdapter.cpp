// (c) https://github.com/MontiCore/monticore
#include "ColorsAdapter.h"

namespace montithings
{
namespace hierarchy
{

Colors::Color
ColorsAdapter::convertColor (uint8_t element)
{
  switch (element)
    {
      case 0:
        return Colors::Color::RED;
      case 1:
        return Colors::Color::GREEN;
      case 2:
        return Colors::Color::BLUE;
      case 3:
        return Colors::Color::YELLOW;
      default:
        return Colors::Color::RED;
    }
}

uint8_t
ColorsAdapter::convertColor (Colors::Color element)
{
  switch (element)
    {
      case Colors::Color::RED:
        return 0;
      case Colors::Color::GREEN:
        return 1;
      case Colors::Color::BLUE:
        return 2;
      case Colors::Color::YELLOW:
        return 3;
      default:
        return 0;
    }
}

} // namespace hierarchy
} // namespace montithings
