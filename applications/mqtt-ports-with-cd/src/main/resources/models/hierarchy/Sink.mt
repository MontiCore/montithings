// (c) https://github.com/MontiCore/monticore
package hierarchy;

import Foo.*;

component Sink {
  port in Foo.Foo value;
  port out Foo.Foo python;

  behavior value {
    log("Sink foo: " + value);
    python = value;
  }
}
