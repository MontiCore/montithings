package hierarchy;

import Foo.*;

component Doubler {
  port in Foo.Foo value;

  port out Foo.Foo py_value;
  port in Foo.Foo py_doubled_value;

  port out Foo.Foo doubled_value;

  behavior value {
    py_value = value;
  }

  behavior py_doubled_value {
      doubled_value = py_doubled_value;
    }
}
