// (c) https://github.com/MontiCore/monticore
#pragma once
#include "tl/optional.hpp"
#include <cereal/types/vector.hpp>
#include <cereal/types/tloptional.hpp>

template <class T>
class PortLink
{
public:
    PortLink(const std::string &fullyQualifiedName) : fullyQualifiedName(fullyQualifiedName) {}
protected:
    std::string fullyQualifiedName;
public:
    const std::string &getFullyQualifiedName() const {
        return fullyQualifiedName;
    }

    template <class Archive>
    void serialize( Archive & ar )
    {
    ar(
      fullyQualifiedName

    );
    }

    bool operator==(const PortLink &rhs) const {
        return fullyQualifiedName == rhs.fullyQualifiedName;
    }

    bool operator!=(const PortLink &rhs) const {
        return !(rhs == *this);
    }
};