<!-- (c) https://github.com/MontiCore/monticore -->
# Serialization with Google Protocol Buffers

MontiThings is able to use Google Protocol Buffers as de-/serialization format for the communication
between components.

## Configuration option

The montithings configuration argument `serialization` lets one select the desired mode of
serialization.
It is set to `JSON` by default.

Set it to `PROTOBUF` to build the application using Protocol Buffers as serialization method.
The build requires the Protocol Buffer compiler _protoc_ and the _libprotobuf_ library to be
installed when building the application.

## Protobuf Serialization Mode

Enabling Protocol Buffers lets cd2cpp emit two additional member functions in the C++ source code
for the class diagram models.
This is done by the
template [proto-methods.ftl](../generators/cd2cpp/src/main/resources/templates/proto-methods.ftl).

The first one is a member function `to_protobuffer()` that serializes and returns the model's
corresponding Protocol Buffer object.
This serialization is done recursively for all non-trivial members of the model's class.

Example of a generated serialization member function:
```cpp
using ProtocolBuffer = FaceUnlock::protobuf::Person;
/// Member method for serialization to Protocol Buffer messages
auto
to_protobuffer () const -> FaceUnlock::protobuf::Person
{
  auto msg = FaceUnlock::protobuf::Person{};
  // Set all fields
  {
    msg.set_name (this->name);
    msg.set_allowed (this->allowed);
    msg.set_visitor_id (this->visitor_id);
  }
  // Set all associations
  {
    // copy pets
    msg.mutable_pets ()->Reserve (this->pets.size ());
    std::transform (this->pets.cbegin (), this->pets.cend (), msg.mutable_pets ()->begin (),
                    [] (const Cat &item) { return make_protobuffer (item); });
  }
  return msg;
}
```

The second member function is a constructor that takes the corresponding Protocol Buffer object as
argument and deserializes the model recursively from it.
Deserializing in the constructor is especially handy since we can initialize the parent class
from here using its Protocol Buffer constructor again.
Together with the design decision to embed the complete parent class this allows to fully
reconstruct derived classes.
It also gives access to all members of the models class which is needed for proper deserialization.

Example of a generated deserializing constructor:
```cpp
using ProtocolBuffer = FaceUnlock::protobuf::Person;
/// Constructor for deserialization from Protocol Buffer messages
explicit Person (const FaceUnlock::protobuf::Person &other)
    : name{ other.name () }, allowed{ other.allowed () }, visitor_id{ other.visitor_id () }
{
  {
    // copy pets
    this->pets.reserve (other.pets ().size ());
    std::transform (other.pets ().cbegin (), other.pets ().cend (), this->pets.begin (),
                    [] (const FaceUnlock::protobuf::Cat &item) { return Cat{ item }; });
  }
}
```

## MontiThings Runtime Environment

In order to support multiple serialization modes `the MqttPort<T>` uses the `Serializer<T>`
interface template.
This way `MqttPort<T>` does not depend on the specific serialization method used.
The actual Object that implements `Serializer<T>` gets passed to `MqttPort<T>` as constructor
argument.

Using a templatized interface allows to provide specializations of the serializer for specific
types.
For example this allowed special handling for the serialization of the class template `Message<T>`.
This class template is used to wrap the actual payload into a control message envelope.
Because existing tools expect the actual payload to be enclosed in a JSON-serialized `Message<T>`,
such a partial specialization was implemented to serialize the envelope as JSON but the inner
payload as base64 encoded Protocol Buffer string.
