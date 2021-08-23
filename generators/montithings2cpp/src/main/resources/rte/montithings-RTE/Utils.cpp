/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#include "Utils.h"

std::string
replaceDotsBySlashes (std::string input)
{
  std::string dot = ".", slash = "/";

  size_t pos;
  while ((pos = input.find (dot)) != std::string::npos)
    {
      input.replace (pos, 1, slash);
    }

  return input;
}

std::string
replaceSlashesByDots (std::string input)
{
    std::string dot = ".", slash = "/";

    size_t pos;
    while ((pos = input.find (slash)) != std::string::npos)
    {
        input.replace (pos, 1, dot);
    }

    return input;
}


std::string
getEnclosingComponentName (const std::string& input)
{
  std::size_t found = input.find_last_of ('.');
  return input.substr (0, found);
}

std::string getModelInstanceName(const std::string& instanceName) {
  int lastDot = instanceName.find_last_of('.');
  if (lastDot != std::string::npos && lastDot != instanceName.length() - 1) {
    try {
      // extract part behind last dot
      std::string lastPart = instanceName.substr(lastDot + 1);
      // try parsing last part as number
      std::stoi(lastPart);
      // return instanceName without the trailing dot (1) and lastPart
      // (lastPart.length())
      return instanceName.substr(0, instanceName.length() - lastPart.length() - 1);
    } catch (std::logic_error e) {
      // last part is not a number
    }
  }
  return instanceName;
}
