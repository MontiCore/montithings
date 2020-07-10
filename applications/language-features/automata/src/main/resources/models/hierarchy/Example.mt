package hierarchy;

<<timesync>> application Example {
  Source source;
  Mod3 mod;
  Sink sink;

  source.value -> mod.inport;
  mod.outport -> sink.value;

  update interval 1sec;

}