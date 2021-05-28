/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once
#include "sole/sole.hpp"
#include "tl/optional.hpp"

template <class T>
class Message {
private:
    tl::optional<T> payload;
    sole::uuid uuid{};

public:
    Message(T payload, const sole::uuid &uuid) : payload(tl::optional<T>(payload)), uuid(uuid) {}

    explicit Message(T payload) : payload(tl::optional<T>(payload)) {}

    explicit Message(tl::optional<T> payload) : payload(payload) {}

    Message() : payload(tl::nullopt){}

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
};

