// (c) https://github.com/MontiCore/monticore
package hierarchy;

import Foo.*;

component Source {
  port out Foo.Foo value;

  int lastValue = 0;

  every 1s {
    value = :Foo.Foo{
      isschuld = lastValue;
      sebastian = lastValue * 0.2;
    };
    lastValue++;
  }
}