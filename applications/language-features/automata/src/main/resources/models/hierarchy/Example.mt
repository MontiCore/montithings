package hierarchy;

<<timesync>> application Example {
  component Source source;
  component Mod3 mod;
  component Sink sink;

  connect source.value -> mod.inport;
  connect mod.outport -> sink.value;

  control {
    update interval 1sec;
  }

}