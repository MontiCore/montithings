#ifndef MONTITHINGS_RTE_SERIALIZERS_H
#define MONTITHINGS_RTE_SERIALIZERS_H

#include "Utils.h"

template<typename T>
struct Serializer {
  virtual ~Serializer() = default;
  virtual auto serialize(const T& data) -> std::string = 0;
  virtual auto deserialize(const std::string& data) -> T = 0;
};

template<typename T>
class JsonSerializer : public Serializer<T> {
  auto serialize(const T& data) -> std::string override {
    return dataToJson(data);
  };

  auto deserialize(const std::string& data) -> T override {
    return jsonToData<T>(data);
  }
};

/// This should allow us to specialize stuff like std::vector, etc.
template<typename T>
auto make_protobuffer(const T& data) -> typename T::ProtocolBuffer {
    return data.make_protobuffer();
}

template<typename CDMessage>
class ProtobufSerializer : public Serializer<CDMessage> {
  // Crazy C++ type stuff to get a hand on the corresponding protocol buffer class
  using ProtocolBuffer = typename CDMessage::payload_type::ProtocolBuffer;
  using CDType = typename CDMessage::payload_type;

  auto serialize(const CDMessage & data) -> std::string override {
      ProtocolBuffer buffer = make_protobuffer(data.getPayload().value());
      // The reference claims that SerializeAsString returns an empty string on error.
      // But a buffer filled with default values also gives us an empty string.
      // When parsing from an empty string protobuf yields a protocol buffer object with default
      // values.
      auto payload = buffer.SerializeAsString();

      // Putting the serialized message into the JSON-Message-Envelope is a bit of a hack but at least
      // we won't break anything this way.
      return dataToJson(Message<std::string>{ payload });
    }

  auto deserialize(const std::string& data) -> CDMessage override {
    auto envelope = jsonToData<Message<std::string>>(data);

    auto buffer = ProtocolBuffer{};
    if (!buffer.ParseFromString(envelope.getPayload().value())) {
        // TODO: Handle failed parsing :(
        throw std::exception{};
    }
    auto msg = CDMessage{};
    msg.setUuid(envelope.getUuid());
    msg.setPayload(CDType{buffer});
    return msg;
  }
};

#endif