/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore
#pragma once
#include "sole/sole.hpp"
#include "tl/optional.hpp"

template <class T>
class Message {
private:
    T payload;
    sole::uuid uuid{};

public:
    Message(T payload, const sole::uuid &uuid) : payload(payload), uuid(uuid) {}

    explicit Message(T payload) : payload(payload) {}

    Message() = default;

    tl::optional<T> getPayload() const {
        return tl::optional<T>(payload);
    }

    void setPayload(T p) {
        Message::payload = p;
    }

    const sole::uuid &getUuid() const {
        return uuid;
    }

    void setUuid(const sole::uuid &id) {
        Message::uuid = id;
    }
};
