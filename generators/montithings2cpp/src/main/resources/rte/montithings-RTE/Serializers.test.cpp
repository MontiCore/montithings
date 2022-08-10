//
// Created by sebastian on 01.07.22.
//

#include "Serializers.h"

#include <ostream>

#define CATCH_CONFIG_MAIN
#include "catch2/catch.hpp"

#include "Message.h"

TEST_CASE("JSON serialization deserialization") {
  SECTION("of Message<int>") {
    using msg_t = Message<int>;
    using json_serializer_t = const JsonSerializer<msg_t>;
    const std::unique_ptr<const Serializer<msg_t>> serializer =
        std::unique_ptr<json_serializer_t>{new json_serializer_t{}};

    const auto msg = msg_t{42};
    const auto json = serializer->serialize(msg);
    const auto msg2 = serializer->deserialize(json);

    REQUIRE(msg.getPayload() == msg2.getPayload());
    REQUIRE(msg.getUuid() == msg2.getUuid());
  }

  SECTION("of int") {
    using msg_t = int;
    using json_serializer_t = const JsonSerializer<msg_t>;
    const std::unique_ptr<const Serializer<msg_t>> serializer =
        std::unique_ptr<json_serializer_t>{new json_serializer_t{}};

    const auto msg = msg_t{42};
    const auto json = serializer->serialize(msg);
    const auto msg2 = serializer->deserialize(json);

    REQUIRE(msg == msg2);
  }
}

template <typename T> struct FakeProtocolBuffer {
  T m_value;
  // Ugly but gets its job done.
  static bool m_nextParseFails;

  auto SerializeAsString() -> std::string {
    auto converter = std::stringstream{};
    converter << m_value;
    return converter.str();
  }

  auto ParseFromString(const std::string &input) -> bool {
    auto converter = std::stringstream{input};
    converter >> m_value;
    if (m_nextParseFails) {
      m_nextParseFails = false;
      return false;
    }
    return true;
  }
};

template <typename T> bool FakeProtocolBuffer<T>::m_nextParseFails = false;

template <typename T> struct FakeCDType {
  using ProtocolBuffer __attribute__((unused)) = FakeProtocolBuffer<T>;

  T value;

  explicit FakeCDType(T value) : value{value} {}
  explicit FakeCDType(FakeProtocolBuffer<T> buffer) : value{buffer.m_value} {}

  auto to_protobuffer() const -> FakeProtocolBuffer<T> {
    return FakeProtocolBuffer<T>{value};
  }

  auto operator==(const FakeCDType &rhs) const -> bool {
    return value == rhs.value;
  }

  friend auto operator<<(std::ostream &out, const FakeCDType &type)
      -> std::ostream & {
    out << "m_value: " << type.value;
    return out;
  }
};

TEST_CASE("Protobuf serializer") {
  SECTION("does serialization deserialization of int") {
    using msg_t = FakeCDType<int>;
    using serializer_t = const ProtobufSerializer<msg_t>;
    const std::unique_ptr<const Serializer<msg_t>> serializer =
        std::unique_ptr<serializer_t>{new serializer_t{}};

    const auto msg = msg_t{42};
    const auto json = serializer->serialize(msg);
    const auto msg2 = serializer->deserialize(json);

    REQUIRE(msg == msg2);
  }

  SECTION(
      "does serialization deserialization of the specialization Message<T>") {
    using msg_t = Message<FakeCDType<int>>;
    using serializer_t = const ProtobufSerializer<msg_t>;
    const std::unique_ptr<const Serializer<msg_t>> serializer =
        std::unique_ptr<serializer_t>{new serializer_t{}};

    const auto msg = msg_t{FakeCDType<int>{1337}};
    const auto json = serializer->serialize(msg);
    const auto msg2 = serializer->deserialize(json);

    REQUIRE(msg.getPayload() == msg2.getPayload());
    REQUIRE(msg.getUuid() == msg2.getUuid());
  }

  SECTION("throws parse_error when parsing fails") {
    using msg_t = FakeCDType<int>;
    using serializer_t = const ProtobufSerializer<msg_t>;

    const std::unique_ptr<const Serializer<msg_t>> serializer =
        std::unique_ptr<serializer_t>{new serializer_t{}};

    const auto msg = msg_t{42};
    const auto json = serializer->serialize(msg);

    FakeProtocolBuffer<int>::m_nextParseFails = true;
    REQUIRE_THROWS_MATCHES(
        serializer->deserialize(json), parse_error,
        Catch::Message(
            "parse_error: Protocol Buffer failed to parse from string"));
  }
}
