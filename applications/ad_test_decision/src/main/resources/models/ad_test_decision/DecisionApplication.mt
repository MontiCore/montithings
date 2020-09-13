package ad_test_decision;

<<timesync>> application DecisionApplication {

  component Source source7, source9, source11;

  component Sink sink5, sink6, sink7, sink8, sink9, sink10, sink11, sink12, sink13;


  connect
  source7.outport ->
    if [source7.outport > 3] : sink5.inport
    if [source7.outport > 4] : sink6.inport;

  connect
  source9.outport ->
    sink7.inport, sink8.inport
    if [source9.outport > 5] : sink9.inport;

  connect
  source11.outport ->
    if [source11.outport >= 10] : sink10.inport
    else if [source11.outport >= 7] : sink11.inport
    else if [source11.outport >= 3] : sink12.inport
    else : sink13.inport;

  control {
    update interval 1sec;
  }

}