/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */
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
