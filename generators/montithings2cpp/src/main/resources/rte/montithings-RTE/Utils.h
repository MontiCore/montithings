/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

#include <string>
#include "tl/optional.hpp"
#include "cereal/archives/json.hpp"
#include "cereal/types/string.hpp"
#include "cereal/types/utility.hpp"
#include "cereal/types/tloptional.hpp"
#include "cereal/types/vector.hpp"
#include "cereal/types/map.hpp"
#include "cereal/types/unordered_map.hpp"
#include "sole/sole.hpp"

template <class T> class Message;

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

template < class T >
std::ostream& operator << (std::ostream& os, const tl::optional<T>& v)
{
  if (v.has_value()) os << v.value();
  else os << "tl::nullopt";
  return os;
}

template < class T >
std::ostream& operator << (std::ostream& os, const std::vector<Message<T>>& v)
{
  os << "[";
  for (auto i = v.begin(); i != v.end(); i++)
    {
      os << *i << (i != v.end()-1 ? ", " : "");
    }
  os << "]";
  return os;
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
 * Replaces slashes in the string with dots
 * This is useful when reversing replaceDotsBySlashes()
 *
 * \param input the string whose dots should be replaced
 * \return the input string with each slash "/" being replaced by a dot "."
 */
std::string replaceSlashesByDots (std::string input);

/**
 * Takes a fully qualified name of a component throws away everything after and including
 * the last dot in the fully qualified name
 * \param input the fully qualified name of a component
 * \return the fully qualified name of the enclosing component
 */
std::string getEnclosingComponentName (const std::string& input);

/**
 * In deployment, the name of instances may end with a number (e.g.
 * hierarchy.test.1) to allow for multi-instantiation. This method removes
 * such number (if present).
 * \param instanceName the fully qualified name of a component
 * \return the fully qualified name without last trailing number part
 */
std::string getModelInstanceName(const std::string& instanceName);

/* Additional serialization functions for cereal */
namespace cereal
{
    // for type sole::uuid
    template<class Archive>
    std::string
    save_minimal(Archive & archive,
                 sole::uuid const & uuid)
    {
        return uuid.str();
    }

    template<class Archive>
    void
    load_minimal(Archive & archive,
                 sole::uuid & uuid,
                 std::string const & value)
    {
        uuid = sole::rebuild(value);
    }

}