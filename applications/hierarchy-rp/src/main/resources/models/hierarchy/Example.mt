package hierarchy;

<<timesync>> application Example {

  resource port in  int inPort  ("ipc://source.ipc");
  resource port out int outPort ("ws://localhost:8080/out/Port/");
  resource port in  int inPort2  ("ws://localhost:8080/out/Port/");

  //component Source source;
  component Sink sink;
  component LowPassFilter (5) lpf;
  component Converter c;
  component Double d;

  connect inPort -> lpf.inport;
  connect lpf.outport -> c.inport;
  connect c.outport -> outPort;
  connect inPort2 -> d.x;
  connect d.y -> sink.value;

  control {
    update interval 1s;
  }

}