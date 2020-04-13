/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

#include <string>
#include "tl/optional.hpp"
#include "cereal/archives/json.hpp"

template<typename T>
std::string
dataToJson (tl::optional<T> dataOpt)
{
  T data = dataOpt.value ();
  std::ostringstream stream;
  {
    cereal::JSONOutputArchive outputArchive (stream);
    outputArchive (data);
  }
  return stream.str ();
}

template<typename T>
T
jsonToData (std::string json)
{
  std::stringstream inStream (json);
  cereal::JSONInputArchive inputArchive (inStream);
  T result;
  inputArchive (result);
  return result;
}

template<typename T>
T
jsonToData (char *json)
{
  return jsonToData<T>((std::string(json)));
}