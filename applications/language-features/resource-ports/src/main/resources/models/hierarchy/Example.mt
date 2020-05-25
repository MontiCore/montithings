package hierarchy;

<<timesync>> application Example {

  resource port in  int inPort  ("ipc://source.ipc");
  resource port out int outPort ("ws://localhost:8080/out/Port/");
  resource port in  int inPort2  ("ws://localhost:8080/out/Port/");

  component Double d;
  component Sink sink;

  connect inPort -> d.x;
  connect d.y -> outPort;
  connect inPort2 ->  sink.value;

  control {
    update interval 1sec;
  }

}