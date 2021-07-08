/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

/*
 * Components exchange data over well defined ports. 
 * This class defines the actual data structure which is exchanged between components.
 * Each message carries an UUID which can be used e.g. for tracing purposes.
 */

#pragma once
#include "sole/sole.hpp"
#include "tl/optional.hpp"

template <class T>
class Message {
private:
    tl::optional<T> payload;
    sole::uuid uuid{};

public:
    // For simplier use, multiple constructors are allowed. The payload can be empty but the uuid cannot be null. 
    Message(T payload, const sole::uuid &uuid) : payload(tl::optional<T>(payload)), uuid(uuid) {}

    explicit Message(T payload) : payload(tl::optional<T>(payload)), uuid(sole::uuid4()) {}

    explicit Message(const sole::uuid &uuid) : payload(tl::nullopt), uuid(uuid) {}

    explicit Message(tl::optional<T> payload) : payload(payload), uuid(sole::uuid4()) {}

    Message() : payload(tl::nullopt), uuid(sole::uuid4()){}

    tl::optional<T> getPayload() const {
        return payload;
    }
    
    void setPayload(T p) {
        Message::payload = tl::optional<T>(p);
    }

    const sole::uuid &getUuid() const {
        return uuid;
    }

    void setUuid(const sole::uuid &id) {
        Message::uuid = id;
    }

    Message<T> applyConversionFactor(double factor) {
        if (payload.has_value()) {
            payload = tl::optional<T>(payload.value() * factor);
        }
        return *this;
    }
};

