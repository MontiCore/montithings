// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source {
  port out InPort.InPort value;
  port in OutPort.OutPort ret;

  int lastValue = 0;

  every 1s {
    lastValue++;
    log("Source: send");

    value = :InPort.InPort{
      text = "Hello: ";
      val = true;
      num = lastValue;
    };
  }

  behavior ret {
    log("Source (ret): " + ret.num);
  }
}
