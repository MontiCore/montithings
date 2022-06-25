// (c) https://github.com/MontiCore/monticore
package hierarchy;

import Foo.*;

component Source {
  port out Foo.Foo value;

  int lastValue = 0;

  every 1s {
    log("Source: " + lastValue);
    // value = lastValue++;
  }
}
