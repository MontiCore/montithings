// (c) https://github.com/MontiCore/monticore
#pragma once
#include "tl/optional.hpp"
#include <cereal/types/tloptional.hpp>
#include <cereal/types/vector.hpp>
#include <ostream>
#include <utility>

/*
 * Components may exchange their interfaces via ports. In this case, the
 * component receiving the interface needs to know how to connect to the
 * received bundle of ports.
 * This class defines the information needed for connecting to the according
 * port from an external device. You can think of it as a "business card"
 * containing contact information for a port.
 *
 * This class does explicitly not include information about the port's type or
 * direction. Compatibility is already checked at the model level by context
 * conditions. This class is exclusively for exchanging communication-related
 * information.
 *
 * By default, ports are only referred to by their fully qualified name. For
 * different communication technologies, developers may extend this class to
 * reflect the information needed to connect using the different communication
 * technology. For example, when using websockets, an IP address and a port
 * number may be needed, and when using the CAN bus, a CAN ID may be needed.
 */
class PortLink
{
public:
  /// Constructors
  PortLink () = default;
  explicit PortLink (std::string fullyQualifiedName)
      : fullyQualifiedName (std::move (fullyQualifiedName))
  {
  }

protected:
  /// Fully-qualified name of the according port
  std::string fullyQualifiedName;

public:
  const std::string &
  getFullyQualifiedName () const
  {
    return fullyQualifiedName;
  }

  template <class Archive>
  void
  serialize (Archive &ar)
  {
    ar (fullyQualifiedName);
  }

  bool
  operator== (const PortLink &rhs) const
  {
    return fullyQualifiedName == rhs.fullyQualifiedName;
  }

  bool
  operator!= (const PortLink &rhs) const
  {
    return !(rhs == *this);
  }

  friend std::ostream &
  operator<< (std::ostream &os, const PortLink &link)
  {
    os << "{ ";
    os << "\"fullyQualifiedName\": \"" << link.fullyQualifiedName << "\"";
    os << " }";

    return os;
  }
};