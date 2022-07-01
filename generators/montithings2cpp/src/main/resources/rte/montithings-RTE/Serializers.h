#ifndef MONTITHINGS_RTE_SERIALIZERS_H
#define MONTITHINGS_RTE_SERIALIZERS_H

#include "Utils.h"
#include <stdexcept>
#include <string>

class parse_error : public std::exception {
  std::string m_msg;

public:
  explicit parse_error(const std::string& msg) : m_msg{"parse_error: "} {
    m_msg.append(msg);
  };

  auto what() const noexcept -> const char * final { return m_msg.c_str(); }
};

template <typename T> class Serializer {
public:
  // The virtual destructor disables implicitly defined operations, so we
  // re-enable them.
  Serializer() = default;
  Serializer(const Serializer<T> &) = default;
  Serializer(Serializer<T> &&) noexcept = default;
  auto operator=(const Serializer<T> &) -> Serializer<T> & = default;
  auto operator=(Serializer<T> &&) noexcept -> Serializer<T> & = default;

  virtual ~Serializer() = default;
  virtual auto serialize(const T &data) const -> std::string = 0;
  virtual auto deserialize(const std::string &data) const -> T = 0;
};

template <typename T> class JsonSerializer : public Serializer<T> {
public:
  auto serialize(const T &data) const -> std::string final {
    return dataToJson(data);
  };

  auto deserialize(const std::string &data) const -> T final {
    return jsonToData<T>(data);
  }
};

/// This should allow us to specialize stuff like std::vector, etc.
template <typename T>
auto make_protobuffer(const T &data) -> typename T::ProtocolBuffer {
  return data.make_protobuffer();
}

template <typename CDType>
class ProtobufSerializer : public Serializer<CDType> {
  using ProtocolBuffer = typename CDType::ProtocolBuffer;

public:
  auto serialize(const CDType &data) const -> std::string final {
    // The reference claims that SerializeAsString returns an empty string on
    // error. But a buffer filled with default values also gives us an empty
    // string. When parsing from an empty string protobuf yields a protocol
    // buffer object with default values.
    return make_protobuffer(data).SerializeAsString();
  }

  auto deserialize(const std::string &data) const -> CDType final {
    auto buffer = ProtocolBuffer{};
    if (!buffer.ParseFromString(data)) {
      // TODO: Handle failed parsing :(
      throw parse_error{"Protocol Buffer failed to parse from string"};
    }
    return CDType{buffer};
  }
};

template <typename CDPayload>
class ProtobufSerializer<Message<CDPayload>>
    : public Serializer<Message<CDPayload>> {
  const ProtobufSerializer<CDPayload> mInner{};

public:
  auto serialize(const Message<CDPayload> &data) const -> std::string final {
    const auto payload = mInner.serialize(data.getPayload().value());
    // Putting the serialized message into the JSON-Message-Envelope is a bit of
    // a hack but at least we won't break anything this way.
    const auto msg = Message<std::string>{payload, data.getUuid()};
    return dataToJson(msg);
  }

  auto deserialize(const std::string &data) const -> Message<CDPayload> final {
    // Extract the actual payload from the Message envelope
    const auto envelope = jsonToData<Message<std::string>>(data);
    const auto actual_payload = envelope.getPayload().value();

    // Deserialize
    const auto buffer = mInner.deserialize(actual_payload);

    // Return a suitable typed Message object
    return Message<CDPayload>{CDPayload{buffer}, envelope.getUuid()};
  }
};

#endif // MONTITHINGS_RTE_SERIALIZERS_H
