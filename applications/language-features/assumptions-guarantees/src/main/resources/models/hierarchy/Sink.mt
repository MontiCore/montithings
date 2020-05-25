package hierarchy;

component Sink {

  port in int value;

  // Comment out the guarantee in the Sink component
  // to let this assumption trigger
  assume : value < 5;
}
