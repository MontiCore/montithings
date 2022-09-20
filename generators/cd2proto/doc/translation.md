<!-- (c) https://github.com/MontiCore/monticore -->
## Translation of Class Diagrams to Protocol Buffer Descriptions

A goal of cd2proto is to have wide support for most features of the cd4analysis class diagram
language.

cd2proto generates descriptions compatible with the version 3 of the Protocol Buffer language.

The following section describes how the different constructs of class diagrams are translated into
corresponding Protocol Buffer messages.

### Simple types

Class diagrams use the primitive data types from Java.
These have to be translated into one of the _Scalar Value Types_ of Google Protocol Buffers.
It is important not to introduce data loss by narrowing down the size of the type representation.

The following table shows the mapping of primitive types and the String class:

| Simple Java type | Protobuf Scalar Value type |
|------------------|----------------------------|
| `int`            | `int32`                    |
| `long`           | `int64`                    |
| `boolean`        | `bool`                     |
| `byte`           | `int32`                    |
| `char`           | `uint32`                   |
| `double`         | `double`                   |
| `float`          | `float`                    |
| `short`          | `int32`                    |
| `String`         | `string`                   |

### Classes

Each class is translated into a new Protocol Buffer Message Type of the same name.
The attributes of the class are represented in the Protocol Buffer using fields.

Example:
```cd
// cd4analysis
class Person {
  [...]
}
```
```protobuf
// Protocol Buffer
message Person {
  [...]
}
```

### Enumerations

The Protocol Buffer language has native support for enumeration types.
These are translated analogous to the translation of classes.

Example:
```cd
// cd4analysis
enum NameOfEnum {
  SOME_VALUE,
  ANOTHER_VALUE;
}
```
```protobuf
// Protocol Buffer
enum NameOfEnum {
    SOME_VALUE = 0;
    ANOTHER_VALUE = 1;
}
```

### Composition

A field of a Protocol Buffer Message may have the type of other Message Types.
Thus, the translation simply embeds the contained class.

Example:
```cd
// cd4analysis
class Node {
  Point point;
}
class Point {
  double x;
  double y;
}
```
```protobuf
// Protocol Buffer
message Node {
  Point point = 1;
}
message Point {
  double x = 1;
  double y = 2;
}
```

### Inheritance

Class diagrams can express inheritance hierarchies of classes.
In cd2proto we reserve the field name `super` to embed the parent class into the Protocol Buffer
Message as we do with composition.

Example:
```cd
// cd4analysis
class A {
}
class B extends A {
}
```
```protobuf
// Protocol Buffer
message A {
}
message B {
  A super = 1;
}
```

The approach to reserve a field named `super` limits cd2proto to represent single inheritance
hierarchies only.
But since cd4analysis does not permit multiple inheritance this should not become a problem.
The benefit of this restriction is simplicity of implementation and usage.

### Arrays and Collections


The class diagram language supports array types and the generic collections `List<>`, `Set<>`, `Optional<>` and `Map<,>`.

Sequences are be expressed in Protocol Buffers using the `repeated` field rule.
Array, `List<>`, `Set<>` and `Optional<>` types are translated into `repeated` fields of the contained type.

Example:
```cd
// cd4analysis
class WithArraysAndCollections {
  int[] primitive_array;
  List<int> primitive_list;
  Map<int, int> primitive_map;
  Set<int> primitive_set;
  Optional<int> primitive_optional;
}
```
```protobuf
// Protocol Buffer
message WithArraysAndCollections {
  repeated int32 primitive_array = 1;
  repeated int32 primitive_list = 2;
  map<int32,int32> primitive_map = 3;
  repeated int32 primitive_set = 4;
  repeated int32 primitive_optional = 5;
}
```

Maps have its own type in Protocol Buffers.
Thus cd2proto directly translates maps into the Protocol Buffer `map<,>` type.

#### Note: Nested Collections

Unfortunately Protocol Buffers do not support nested lists and lists of maps.
If necessary one can create intermediate Message types, e.g. for using `Set<Set<int>` create an extra `SetOfint` Message like this:

Example:
```cd
// cd4analysis
class ClassWithNestedList {
  List<List<String>> nestedList;
}
```
```protobuf
// Protocol Buffer
message ClassWithNestedList {
  repeated ListOfString nestedList = 1;
}
message ListOfString {
  repeated ListOfString strings = 1;
}
```

Automatic creation of such intermediate Message Types is currently implemented for `List<>` only.

### Associations

Unidirectional associations are handled similar to composition.
In case of a cardinality of exactly 1 the associated type is just embedded into the Protocol Buffer.

Otherwise, a `repeated` field is used to embed the associated type.

Example:

```cd
// cd4analysis
class SomeClass {}
class OtherClass {}
association SomeClass -> (mult) OtherClass [*];
association SomeClass -> (opt) OtherClass [0..1];
association SomeClass -> (single) OtherClass [1];
```

```protobuf
// Protocol Buffer
message SomeClass {
  repeated OtherClass mult = 1;
  repeated OtherClass opt = 2;
  OtherClass single = 3;
}
message OtherClass {
}
```

#### Note: Cyclic Associations

Bidirectional, cyclic and unspecified associations are _not supported_ by cd2proto as these might
result in cyclic dependencies.
Serialization of cyclic data structures is non-trivial and hard to handle with code generators.
For further information see:
TODO: Reference to Andres CoCo description

### Packages

It is not necessary to translate the package name, because Protocol Buffer descriptions have the same notion of packages as class diagrams.

### Interfaces

Interfaces in cd4analysis only describe methods. 
cd2proto does not translate interfaces because they do not carry any data that should be serialized.

### Reserved Message Fields and Enumeration Values

The Protocol Buffer language has a concept of field numbers where each field of a Message or Enum
Type is assigned a unique number.
Field numbers are used to allow updates of Protocol Buffer descriptions without breaking existing
code that is not aware of these changes.

To make use of this feature one has to manually manage the field numbers.
Since class diagrams do not provide this information, cd2proto _does not_
follow [Protocol Buffer's rules to safely update a message type](https://developers.google.com/protocol-buffers/docs/proto3#updating)
.
