/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

#include <string>
#include "tl/optional.hpp"
#include "cereal/archives/json.hpp"
#include "cereal/types/unordered_map.hpp"

template<typename T>
std::string
dataToJson (T data)
{
  std::ostringstream stream;
  {
    cereal::JSONOutputArchive outputArchive (stream);
    outputArchive (data);
  }
  return stream.str ();
}

template<typename T>
std::string
dataToJson (tl::optional<T> dataOpt)
{
  T data = dataOpt.value ();
  return dataToJson(data);
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

template<typename T>
std::string
concat(const std::string& first, T second) {
  std::stringstream ss;
  ss << first << second;
  return ss.str();
}

/**
 * Replaces dots in the string with slashes
 * This is useful when fully qualified names should be used in MQTT topics
 *
 * \param input the string whose dots should be replaced
 * \return the input string with each dot "." being replaced by a slash "/"
 */
std::string replaceDotsBySlashes (std::string input);

/**
 * Takes a fully qualified name of a component throws away everything after and including
 * the last dot in the fully qualified name
 * \param input the fully qualified name of a component
 * \return the fully qualified name of the enclosing component
 */
std::string getEnclosingComponentName (const std::string& input);