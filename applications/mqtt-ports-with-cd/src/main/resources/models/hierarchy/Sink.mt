// (c) https://github.com/MontiCore/monticore
package hierarchy;

import Foo.*;

component Sink {
  port in Foo.Foo value;

  behavior value {
    log("Sink: " + value);
  }
}
